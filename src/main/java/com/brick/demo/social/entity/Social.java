package com.brick.demo.social.entity;

import com.brick.demo.auth.entity.Account;
import com.brick.demo.common.entity.BaseEntity;
import com.brick.demo.social.dto.SocialCreateRequest;
import com.brick.demo.social.dto.SocialUpdateRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "social")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Social extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "name", nullable = false, length = 20)
  private String name;

  @Column(name = "gathering_date", nullable = false)
  private LocalDateTime gatheringDate;

  @Column(name = "address")
  @Setter
  private String address;

  @Column(name = "image_urls")
  @Setter
  private String imageUrls;

  @Column(name = "tags")
  private String tags;

  @Column(name = "min_count", nullable = false)
  private Integer minCount;

  @Column(name = "max_count", nullable = false)
  private Integer maxCount;

  @Column(name = "dues", nullable = false)
  private Integer dues = 0;

  @Column(name = "canceled")
  @Setter
  private Boolean canceled;

  @OneToOne(
      mappedBy = "social",
      cascade = CascadeType.ALL,
      fetch = FetchType.LAZY,
      optional = false)
  @Setter
  private SocialDetail detail;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "account_id", nullable = false)
  private Account owner;

  @OneToMany(mappedBy = "social", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SocialParticipant> participants = new HashSet<>();

  public Social(
      String name,
      LocalDateTime gatheringDate,
      String address,
      String imageUrls,
      String tags,
      Integer min,
      Integer max,
      Integer dues,
      Account account) {
    this.name = name;
    this.gatheringDate = gatheringDate;
    this.address = address;
    this.imageUrls = imageUrls;
    this.tags = tags;
    this.minCount = min;
    this.maxCount = max;
    this.dues = dues;
    this.owner = account;
  }

  public static Social save(final Account account, final SocialCreateRequest dto) {
    String address = dto.place().address() + ' ' + dto.place().detailAddress();
    String imageUrls = dto.imageUrls() == null ? null : String.join(",", dto.imageUrls());
    String tags = dto.tags() == null ? null : String.join(",", dto.tags());

    return new Social(
        dto.name(),
        dto.gatheringDate(),
        address,
        imageUrls,
        tags,
        dto.participantCount().min(),
        dto.participantCount().max(),
        dto.dues(),
        account);
  }

  public static Social update(Social social, final SocialUpdateRequest dto) {
    social.address = dto.place().address() + ' ' + dto.place().detailAddress();
    social.imageUrls = String.join(",", dto.imageUrls());
    return social;
  }

  public static Social cancel(Social social) {
    social.canceled = true;
    return social;
  }

  public static Social updateDetail(Social social, final SocialDetail detail) {
    social.detail = detail;
    return social;
  }
}
