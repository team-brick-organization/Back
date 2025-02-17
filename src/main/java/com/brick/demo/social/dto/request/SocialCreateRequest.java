package com.brick.demo.social.dto.request;

import com.brick.demo.social.dto.common.ParticipantCount;
import com.brick.demo.social.dto.common.Place;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public record SocialCreateRequest(
		@Schema(description = "모임 이름", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty String name,

		@Schema(description = "모임 소개", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotEmpty String description,

		@Schema(description = "모임 일정", requiredMode = Schema.RequiredMode.REQUIRED)
		@NotNull LocalDateTime gatheringDate,

		@Schema(description = "모임 정원", requiredMode = Schema.RequiredMode.REQUIRED)
		@Valid ParticipantCount participantCount,

		@Schema(description = "장소 정보", requiredMode = Schema.RequiredMode.REQUIRED)
		@Valid Place place,

		@Schema(description = "이미지 URL 배열")
		List<String> imageUrls,

		@Schema(description = "활동비", requiredMode = RequiredMode.NOT_REQUIRED)
		Integer dues,

		@Schema(description = "태그", requiredMode = Schema.RequiredMode.REQUIRED)
		List<String> tags
) {
	
    public SocialCreateRequest(final String name, final String description, final LocalDateTime gatheringDate, final ParticipantCount participantCount, final Place place) {
        this(name, description, gatheringDate, participantCount, place, null, 0, null);
    }
}
