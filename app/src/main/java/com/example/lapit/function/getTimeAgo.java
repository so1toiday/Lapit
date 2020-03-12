package com.example.lapit.function;

public class getTimeAgo {
    private static final int SECOND=1000;
    private static final int MINUTE=SECOND*60;
    private static final int HOUR=MINUTE*60;
    private static final int DAY=HOUR*24;


    public static String getTime(long time){

        long now=System.currentTimeMillis();
        if(now<time || time<=0){
            return null;
        }

        //TODO: localize
        final long diff=now-time;
        if(diff<MINUTE){
            return "vừa nãy";
        }else if (diff<HOUR){
            long mMinute=diff/MINUTE;
            return mMinute+ " phút trước";
        }else if(diff<DAY) {
            long mHOUR = diff / HOUR;
            return mHOUR + " giờ trước";
        }else if(diff>DAY){
            long mDay=diff/DAY;
            return mDay+" ngày trước";
        }
        return "";
    }

}
