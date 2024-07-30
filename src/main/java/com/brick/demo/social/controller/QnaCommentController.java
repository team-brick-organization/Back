package com.brick.demo.social.controller;

import com.brick.demo.common.dto.PaginationIdResponse;
import com.brick.demo.social.dto.QnaCommentPatchDto;
import com.brick.demo.social.dto.QnaCommentRequestDto;
import com.brick.demo.social.dto.QnaCommentResponseDto;
import com.brick.demo.social.service.QnaCommentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/socials/{socialId}/qnas/{qnaId}/comments")
public class QnaCommentController implements QnaCommentControllerDocs {

	private final QnaCommentService qnaCommentService;

	@Autowired
	QnaCommentController(QnaCommentService qnaCommentService) {
		this.qnaCommentService = qnaCommentService;
	}

	@GetMapping // qna id가 커서 (오래된순)
	public PaginationIdResponse getCommentsByQnaId(@PathVariable Long qnaId,
			@RequestParam(value = "cursor", required = false) Long cursor,
			@RequestParam(value = "limit") int limit) {
		return qnaCommentService.getCommentsByQnaId(qnaId, cursor, limit);
	}

	@PostMapping
	public QnaCommentResponseDto create(@PathVariable Long qnaId,
			@Valid @RequestBody QnaCommentRequestDto dto) {
		return qnaCommentService.create(qnaId, dto);
	}

	@PatchMapping("/{commentId}")
	public QnaCommentResponseDto update(@PathVariable Long commentId,
			@Valid @RequestBody
			QnaCommentPatchDto dto) {
		return qnaCommentService.update(commentId, dto);
	}

	@DeleteMapping("/{commentId}")
	public ResponseEntity<Void> delete(@PathVariable Long commentId) {
		qnaCommentService.delete(commentId);
		return ResponseEntity.noContent().build();
	}
}
