package com.cambio_earth.symbiosis.models;

public class MissionViewModel {
    public final Mission mission;
    public final long userProgress; // how many likes or posts the user has done
    public final boolean readyToClaim; // if the reward is ready to claim
    public final boolean rewardClaimed; // if the mission is completed and the reward has been claimed
    public final int progressPercent;

    public MissionViewModel(Mission mission, long userProgress, boolean rewardClaimed) {
        this.mission = mission;
        this.userProgress = userProgress;
        this.rewardClaimed = rewardClaimed;
        this.readyToClaim = (userProgress >= mission.getCompletionReq()) && !rewardClaimed;
        this.progressPercent = (int)(((userProgress / mission.getCompletionReq()) * 100));
    }
}