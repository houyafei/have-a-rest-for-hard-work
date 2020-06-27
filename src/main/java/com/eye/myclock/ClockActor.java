package com.eye.myclock;

import akka.actor.AbstractActorWithTimers;


import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.eye.constant.Constants;
import com.eye.msgInterface.MessageInterface;


import java.time.Duration;
import java.util.Date;

public class ClockActor extends AbstractActorWithTimers {

    private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

    public ClockActor(MessageInterface msgCmd) {
        this.msgCmd = msgCmd;
        getTimers().startSingleTimer("start_key", "start", Duration.ofSeconds(Constants.WORKING_INTERVAL_SECONDS));
    }


    private MessageInterface msgCmd;

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class,
                        s -> {
                            msgCmd.openWin();
                            log.info("get msg: (" + new Date().toString() + ")" + s);
                            getTimers().startPeriodicTimer("start_key", "start", Duration.ofSeconds(Constants.WORKING_INTERVAL_SECONDS+Constants.REST_INTERVAL_SECONDS));
                            getTimers().startSingleTimer("end_key", 1, Duration.ofSeconds(Constants.REST_INTERVAL_SECONDS));
                        })
                .match(Integer.class,
                        num -> {
                            msgCmd.closeWin();
                            log.info("get num: (" + new Date().toString() + ") " + num);
                        })
                .matchAny(o -> log.info("unknown msg"))
                .build();
    }


//    @Override
//    public void postStop()  {
//        super.postStop();
//        log.error(" i am stop now");
//    }
//
//    @Override
//    public void preStart()  {
//        super.preStart();
//        log.info("i am start now");
//    }
//
//    @Override
//    public void postRestart(Throwable reason) {
//        super.postRestart(reason);
//        log.info("i am restart");
//    }
}

