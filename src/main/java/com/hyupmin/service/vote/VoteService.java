package com.hyupmin.service.vote;

import lombok.RequiredArgsConstructor;
import com.hyupmin.domain.post.Post;
import com.hyupmin.domain.user.User;
import com.hyupmin.domain.vote.Vote;
import com.hyupmin.domain.vote.VoteOption;
import com.hyupmin.domain.vote.VoteRecord;
import com.hyupmin.dto.vote.VoteCreateRequest;
import com.hyupmin.dto.vote.VoteResponse;
import com.hyupmin.dto.vote.VoteUpdateRequest;
import com.hyupmin.repository.post.PostRepository;
import com.hyupmin.repository.user.UserRepository;
import com.hyupmin.repository.vote.VoteOptionRepository;
import com.hyupmin.repository.vote.VoteRecordRepository;
import com.hyupmin.repository.vote.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class VoteService {

    private final VoteOptionRepository voteOptionRepository;
    private final VoteRecordRepository voteRecordRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final VoteRepository voteRepository;

    // ----------------- 투표하기 -----------------
    public void castVote(Long optionId, String userEmail) {
        // 1. 옵션 조회
        VoteOption option = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 항목입니다."));
        Vote vote = option.getVote();

        // 2. 마감 여부 체크
        if (vote.getEndTime() != null && LocalDateTime.now().isAfter(vote.getEndTime())) {
            throw new IllegalStateException("이미 마감된 투표입니다.");
        }

        // 3. 유저 조회
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 4. allowMultipleChoices 에 따라 검증 로직 분기
        if (Boolean.FALSE.equals(vote.getAllowMultipleChoices())) {
            // 단일 선택 투표 -> 이미 이 투표에 참여한 적 있으면 막기
            if (voteRecordRepository.existsByUserAndVoteOption_Vote(user, vote)) {
                throw new IllegalStateException("이미 이 투표에 참여했습니다. 재투표 API를 사용하세요.");
            }
        } else {
            // 다중 선택 투표 -> 같은 옵션을 두 번 찍는 것만 막기
            if (voteRecordRepository.existsByUserAndVoteOption(user, option)) {
                throw new IllegalStateException("이미 선택한 항목입니다.");
            }
        }

        // 5. 투표 기록 저장
        VoteRecord record = VoteRecord.builder()
                .user(user)
                .voteOption(option)
                .build();
        voteRecordRepository.save(record);

        // 6. 득표수 +1
        option.increaseCount();
    }

    // ----------------- 투표 생성 -----------------
    public VoteResponse createVote(VoteCreateRequest request, String userEmail) {

        // 1. 게시글 조회
        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 게시글입니다."));

        // (선택) 작성자 확인용 - 투표 생성 권한 체크
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 2. Vote 엔티티 생성
        Vote vote = Vote.builder()
                .post(post)
                .title(request.getTitle())
                .startTime(LocalDateTime.now())
                .endTime(request.getEndTime())
                .allowMultipleChoices(
                        request.getAllowMultipleChoices() != null ? request.getAllowMultipleChoices() : false
                )
                .isAnonymous(
                        request.getIsAnonymous() != null ? request.getIsAnonymous() : false
                )
                .build();

        // 3. 옵션들 생성 및 연관관계 설정
        if (request.getOptionContents() != null) {
            for (String content : request.getOptionContents()) {
                if (content == null || content.isBlank()) continue; // 빈 값은 무시
                VoteOption option = VoteOption.builder()
                        .content(content)
                        .count(0)
                        .build();
                vote.addOption(option); // Vote 쪽에 옵션 추가 (연관관계 세팅)
            }
        }

        // 4. 저장 (cascade 때문에 옵션도 같이 저장됨)
        Vote saved = voteRepository.save(vote);

        return new VoteResponse(saved);
    }

    // ----------------- 재투표 -----------------
    public void reCastVote(Long optionId, String userEmail) {
        VoteOption newOption = voteOptionRepository.findById(optionId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투표 항목입니다."));
        Vote vote = newOption.getVote();

        // 마감 여부 체크
        if (vote.getEndTime() != null && LocalDateTime.now().isAfter(vote.getEndTime())) {
            throw new IllegalStateException("이미 마감된 투표입니다.");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        // 1) 단일 선택 투표일 때 (allowMultipleChoices = false)
        if (Boolean.FALSE.equals(vote.getAllowMultipleChoices())) {

            // 이 유저가 이 투표에서 했던 이전 선택들 전부 조회
            List<VoteRecord> previousRecords =
                    voteRecordRepository.findByUserAndVoteOption_Vote(user, vote);

            // 득표수 줄이고 기록 삭제
            for (VoteRecord record : previousRecords) {
                record.getVoteOption().decreaseCount();
            }
            voteRecordRepository.deleteAll(previousRecords);

            // 새 옵션으로 투표
            VoteRecord newRecord = VoteRecord.builder()
                    .user(user)
                    .voteOption(newOption)
                    .build();
            voteRecordRepository.save(newRecord);
            newOption.increaseCount();

            return;
        }

        // 2) 다중 선택 투표일 때 (allowMultipleChoices = true)
        // 여기서는 "이 옵션에 대한 내 선택을 토글"하는 개념으로 처리

        // 이 유저가 이 옵션에 이미 투표했는지 확인
        boolean alreadyVotedThisOption =
                voteRecordRepository.existsByUserAndVoteOption(user, newOption);

        if (alreadyVotedThisOption) {
            // 이미 선택한 항목이면 → 선택 취소 (득표수 -1 & 기록 삭제)
            List<VoteRecord> records =
                    voteRecordRepository.findByUserAndVoteOption(user, newOption);

            for (VoteRecord record : records) {
                record.getVoteOption().decreaseCount();
            }
            voteRecordRepository.deleteAll(records);

        } else {
            // 아직 선택 안 한 항목이면 → 새로 선택 (득표수 +1 & 기록 추가)
            VoteRecord newRecord = VoteRecord.builder()
                    .user(user)
                    .voteOption(newOption)
                    .build();
            voteRecordRepository.save(newRecord);
            newOption.increaseCount();
        }
    }
}
