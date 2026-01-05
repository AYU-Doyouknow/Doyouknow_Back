package org.ayu.doyouknowback.domain.fcm.form;

import java.util.List;

public class NotificationPushResultDTO {

    public final int totalCount;
    public final int successCount;
    public final List<String> failedTokens;

    public NotificationPushResultDTO(int totalCount, int successCount, List<String> failedTokens) {
        this.totalCount = totalCount;
        this.successCount = successCount;
        this.failedTokens = failedTokens;
    }

    public boolean hasFailedTokens(){
        return !failedTokens.isEmpty();
    }

    public int getFailCount(){
        return totalCount - successCount;
    }
}
