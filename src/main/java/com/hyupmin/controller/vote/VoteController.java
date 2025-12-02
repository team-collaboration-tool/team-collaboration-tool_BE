package com.hyupmin.controller.vote;

import lombok.RequiredArgsConstructor;
import com.hyupmin.dto.vote.VoteResponse;
import com.hyupmin.dto.vote.VoteCreateRequest;
import com.hyupmin.service.vote.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;


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
    public ResponseEntity<VoteResponse> createVote(
            @AuthenticationPrincipal String userEmail,
            @RequestBody VoteCreateRequest request
    ) {
        VoteResponse response = voteService.createVote(request, userEmail);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
    * 투표하기 (생성 후 첫 투표)
     * [Post] /api/votes/options/{optionId}/cast
     * 설명: 투표 생성 후 처음 투표하기 기능을 사용했을 때 작동합니다.
     * */
    @PostMapping("/options/{optionId}/cast")
    public ResponseEntity<String> castVote(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long optionId
    ) {
        voteService.castVote(optionId, userEmail);
        return ResponseEntity.ok("투표가 완료되었습니다.");
    }

    /**
     * 재투표하기 (기존 투표 취소 후 다시 투표)
     * [PUT] /api/votes/options/{optionId}/cast
     * 설명: 기존에 투표한 내역이 있다면 삭제하고, 새로운 optionId로 다시 투표합니다.
     */
    @PutMapping("/options/{optionId}/cast")
    public ResponseEntity<String> reCastVote(
            @AuthenticationPrincipal String userEmail,
            @PathVariable Long optionId
    ) {
        voteService.reCastVote(optionId, userEmail);
        return ResponseEntity.ok("재투표가 완료되었습니다.");
    }


}
