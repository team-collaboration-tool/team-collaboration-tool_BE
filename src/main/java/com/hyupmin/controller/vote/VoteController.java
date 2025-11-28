package com.hyupmin.controller.vote;

import lombok.RequiredArgsConstructor;
import com.hyupmin.dto.vote.VoteResponse;
import com.hyupmin.dto.vote.VoteUpdateRequest; // DTO 필요 (아래 참고)
import com.hyupmin.dto.vote.VoteCreateRequest;
import com.hyupmin.service.vote.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/votes")
@RequiredArgsConstructor
public class VoteController {

    private final VoteService voteService;
    /**
     * 투표 생성
     * [POST] /api/votes
     */
    @PostMapping
    public ResponseEntity<VoteResponse> createVote(@RequestBody VoteCreateRequest request) {
        String tempUserEmail = "testuser@example.com";
        VoteResponse response = voteService.createVote(request, tempUserEmail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * 재투표하기 (기존 투표 취소 후 다시 투표)
     * [PUT] /api/votes/options/{optionId}/cast
     * 설명: 기존에 투표한 내역이 있다면 삭제하고, 새로운 optionId로 다시 투표합니다.
     */
    @PutMapping("/options/{optionId}/cast")
    public ResponseEntity<String> reCastVote(@PathVariable Long optionId) {
        String tempUserEmail = "testuser@example.com";

        // VoteService에 reCastVote 메서드 구현 필요
        // voteService.reCastVote(optionId, tempUserEmail);

        return ResponseEntity.ok("재투표가 완료되었습니다.");
    }

    /**
     * 투표 수정하기 (투표 제목, 마감일 등 설정 변경)
     * [PATCH] /api/votes/{voteId}
     * 설명: 투표 생성자(작성자)만 수정 가능합니다.
     */
    @PatchMapping("/{voteId}")
    public ResponseEntity<String> updateVote(
            @PathVariable Long voteId,
            @RequestBody VoteUpdateRequest request) {

        String tempUserEmail = "testuser@example.com";

        // VoteService에 updateVote 메서드 구현 필요
        // voteService.updateVote(voteId, request, tempUserEmail);

        return ResponseEntity.ok("투표 정보가 수정되었습니다.");
    }

    /**
     * 투표 삭제하기 (옵션)
     * [DELETE] /api/votes/{voteId}
     */
    @DeleteMapping("/{voteId}")
    public ResponseEntity<Void> deleteVote(@PathVariable Long voteId) {
        String tempUserEmail = "testuser@example.com";

        // VoteService에 deleteVote 메서드 구현 필요
        // voteService.deleteVote(voteId, tempUserEmail);

        return ResponseEntity.ok().build();
    }
}
