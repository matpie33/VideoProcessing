package main.utilities;

import org.springframework.stereotype.Component;

@Component
public class BitUtils {

    public boolean isBitSet(byte byteToCheck, int bitIndex){
        return (byteToCheck & (1 <<(7-bitIndex))) !=0;
    }

}
