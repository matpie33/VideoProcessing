package main.videodecoding;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.ByteArrayInputStream;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ExpGolombParserTest {

    private ExpGolombParser expGolombParser;

    @BeforeEach
    public void init (){
        BitReader bitReader = new BitReader();
        expGolombParser = new ExpGolombParser( bitReader);
    }

    @Test
    public void shouldCountLeadingZeros (){
        byte[] ints = {0b00010000};
        int leadingZerosCheckResultDTO = expGolombParser.countLeadingBits(new ByteArrayInputStream(ints));
        Assertions.assertThat(leadingZerosCheckResultDTO).isEqualTo(3);
    }

    @Test
    public void shouldSwitchToNextByte (){
        byte[] ints = {0b00010000, 0b01000000};
        ByteArrayInputStream inputStream = new ByteArrayInputStream(ints);
        int firstResult = expGolombParser.countLeadingBits(inputStream);
        Assertions.assertThat(firstResult).isEqualTo(3);
        int secondResult = expGolombParser.countLeadingBits(inputStream);
        Assertions.assertThat(secondResult).isEqualTo(5);
    }

}