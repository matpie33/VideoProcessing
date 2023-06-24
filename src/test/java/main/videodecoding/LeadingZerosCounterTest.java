package main.videodecoding;

import main.utilities.BitUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LeadingZerosCounterTest {

    private LeadingZerosCounter leadingZerosCounter;

    @BeforeAll
    public void init (){
        leadingZerosCounter = new LeadingZerosCounter(new BitUtils());
    }

    @Test
    public void shouldCountLeadingZeros (){
        byte[] ints = {0b00010000};
        LeadingZerosCheckResultDTO leadingZerosCheckResultDTO = leadingZerosCounter.countLeadingBits(new ByteArrayInputStream(ints));
        Assertions.assertThat(leadingZerosCheckResultDTO.getLeadingZeroBits()).isEqualTo(3);
        Assertions.assertThat(leadingZerosCheckResultDTO.getPreviouslyCheckedByte()).isEqualTo((byte)0b00010000);
        Assertions.assertThat(leadingZerosCheckResultDTO.getPreviouslyCheckedBitIndex()).isEqualTo(3);
    }

    @Test
    public void shouldSwitchToNextByte (){
        byte[] ints = {0b00010000, 0b01000000};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(ints);
        LeadingZerosCheckResultDTO firstResult = leadingZerosCounter.countLeadingBits(inputStream);
        Assertions.assertThat(firstResult.getLeadingZeroBits()).isEqualTo(3);
        LeadingZerosCheckResultDTO secondResult = leadingZerosCounter.countLeadingBits(inputStream);
        Assertions.assertThat(secondResult.getLeadingZeroBits()).isEqualTo(5);
        Assertions.assertThat(secondResult.getPreviouslyCheckedByte()).isEqualTo((byte)0b01000000);
        Assertions.assertThat(secondResult.getPreviouslyCheckedBitIndex()).isEqualTo(1);
    }

}