package com.testnetdeve.custom.alarm;

import com.testnetdeve.custom.client.Client;
import com.testnetdeve.test1.A;

public abstract class AlarmCommandAdapter implements AlarmCommand {

    @Override
    public void execute() {
        //Nothing to do,maybe sub-class override this method
    }
}
