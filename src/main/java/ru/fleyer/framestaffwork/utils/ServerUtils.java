package ru.fleyer.framestaffwork.utils;

import ru.fleyer.framestaffwork.FrameStaffWork;

public class ServerUtils implements Runnable {
	
    private final transient String user;
  
    public ServerUtils(final String user) {
        this.user = user;
    }
    
    @Override
    public void run() {
        FrameStaffWork.getInstance().time.put(this.user, FrameStaffWork.getInstance().time.getOrDefault(this.user, 0) + 1);
    }

}
