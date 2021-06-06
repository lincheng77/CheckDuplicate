package cn.edkso.checkduplicate;

import cn.textcheck.CheckManager;
import org.junit.jupiter.api.Test;

public class CommonTest {

    @Test
    public void getMachineCode(){
        System.out.println(CheckManager.INSTANCE.getMachineCode());
    }
}
