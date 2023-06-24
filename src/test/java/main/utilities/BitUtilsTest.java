package main.utilities;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class BitUtilsTest {


    private BitUtils bitUtils = new BitUtils();

    @Test
    void firstBitTest() {
        int b = 0b10000000;
        boolean bitSet = bitUtils.isBitSet((byte)b, 0);
        boolean notSet = bitUtils.isBitSet((byte)b, 1);
        Assertions.assertThat(bitSet).isTrue();
        Assertions.assertThat(notSet).isFalse();
    }

    @Test
    void lastBitTest() {
        int b = 0b00000001;
        boolean bitSet = bitUtils.isBitSet((byte)b, 7);
        boolean notSet = bitUtils.isBitSet((byte)b, 6);
        Assertions.assertThat(bitSet).isTrue();
        Assertions.assertThat(notSet).isFalse();
    }

    @Test
    void middleBitTest() {
        int b = 0b00010001;
        boolean bitSet = bitUtils.isBitSet((byte)b, 7);
        boolean bitSet2 = bitUtils.isBitSet((byte)b, 3);
        Assertions.assertThat(bitSet).isTrue();
        Assertions.assertThat(bitSet2).isTrue();
    }

}