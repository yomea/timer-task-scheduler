package com.hangu.task;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Created by wuzhenhong on 10/13/21 3:32 PM
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, properties = {"log.home=/Users/wuzhenhong/tmp/task-scheduler"})
public class TaskSchedulerTest {

    @Test
    public void t1() {

    }
}
