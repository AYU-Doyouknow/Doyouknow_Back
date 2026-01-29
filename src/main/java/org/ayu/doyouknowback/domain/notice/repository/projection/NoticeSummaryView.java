package org.ayu.doyouknowback.domain.notice.repository.projection;

/**
 * Repository 내의 Projection
 */
public interface NoticeSummaryView {
    Long getId();
    String getNoticeTitle();
    String getNoticeWriter();
    String getNoticeDate();
    String getNoticeCategory();
}