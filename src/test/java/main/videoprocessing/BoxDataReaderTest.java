package main.videoprocessing;


import main.boxes.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ExtendWith(SpringExtension.class)
@Import(value={BoxDataReader.class})
@ContextConfiguration(classes={TestConfiguration.class})
public class BoxDataReaderTest {

    @Autowired
    private BoxDataReader boxDataReader;

    @Autowired
    private ApplicationContext applicationContext;

    private static final String FILE_NAME = "/screen-capture.mp4";

    @Test
    public void testMe () throws URISyntaxException, IOException, ClassNotFoundException, InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, NoSuchFieldException {
        boxDataReader = new BoxDataReader(new FieldsOrderComparator());
        boxDataReader.setApplicationContext(applicationContext);
        URI path = getClass().getResource(FILE_NAME).toURI();
        List<BasicBox> basicBoxes;
        try (FileInputStream fileInputStream = new FileInputStream(Path.of(path).toFile())) {
            basicBoxes = boxDataReader.readAllBoxes(fileInputStream);
        }
        for (BasicBox basicBox : basicBoxes) {
            if (basicBox instanceof MovieBox){
                Object movieHeaderBox = findField(basicBox, "movieHeaderBox");
                assertFields(movieHeaderBox, getMovieHeaderBoxExpectedValues());
            }
            if (basicBox instanceof FileTypeBox){
                FileTypeBox fileTypeBox = (FileTypeBox) basicBox;
                assertFields(fileTypeBox, getFileTypeBoxFields());
            }
            if (basicBox instanceof TrackBox){
                TrackBox trackBox = (TrackBox) basicBox;
                Object editListBox = findField(trackBox, "editBox", "editListBox");
                Object trackHeaderBox = findField( trackBox, "trackHeaderBox");
                Object mediaBox = findField(trackBox, "mediaBox");

                Object handlerBox = findField(mediaBox, "handlerBox");
                Object mediaHeaderBox = findField(mediaBox, "mediaHeaderBox");
                Object mediaInformationBox = findField(mediaBox, "mediaInformationBox");

                Object dataReferenceBox = findField(mediaInformationBox, "dataInformationBox", "dataReferenceBox");
                Object sampleDescriptionBox = findField(mediaInformationBox, "sampleTableBox", "sampleDescriptionBox");
                Object videoMediaHeaderBox = findField(mediaInformationBox, "videoMediaHeaderBox");

                Object avcSampleEntry = getFirstArrayElement(sampleDescriptionBox, "sampleEntries");
                Object avcConfigurationBox = findField(avcSampleEntry, "avcConfigurationBox");
                Object pictureParameters = getFirstArrayElement(avcConfigurationBox, "pictureParameters");
                Object sequenceParameters = getFirstArrayElement(avcConfigurationBox, "sequenceParameters");
                Object chromaParameters = findField(avcConfigurationBox, "chromaParameters");

                assertFields(sampleDescriptionBox, getSampleDescriptionBoxValues());
                assertFields(avcSampleEntry, getAvcSampleEntryValues());
                assertFields(avcConfigurationBox, getAvcConfigurationBoxValues());
                assertFields(pictureParameters, getPictureParametersValues());
                assertFields(sequenceParameters, getSequenceParametersValues());
                assertFields(chromaParameters, getChromaParameters());
                assertFields(editListBox, getEditListBoxExpectedValues());
                assertFields(handlerBox, getHandlerBoxExpectedValues());
                assertFields(trackHeaderBox, getTrackHeaderBoxExpectedValues());
                assertFields(dataReferenceBox, getDataReferenceBoxValues());
                assertFields(mediaHeaderBox, getMediaHeaderBoxFields());
                assertFields(videoMediaHeaderBox, getVideoMediaHeaderBox());




            }
            System.out.println();

        }

    }

    private Map<String, Object> getSampleDescriptionBoxValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("entryCount", 1);
        map.put("flags", new byte[] {0, 0, 0});
        map.put("version", (byte)0);
        return map;
    }

    private Map<String, Object> getAvcSampleEntryValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("compressorName",new String( new byte [] {21, 76, 97, 118, 99, 53, 57, 46, 51, 55, 46, 49, 48, 48, 32, 108, 105,
                98, 120, 50, 54, 52, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}));
        map.put("dataReferenceIndex", (short)1);
        map.put("depth", (short)24);
        map.put("frameCount", (short)1);
        map.put("height", (short)240);
        map.put("horizontalResolution", 4718592);
        map.put("predefined", (short)0);
        map.put("predefined3",(short) -1);
        map.put("reserved", (short)0);
        map.put("reserved2", 0);
        map.put("verticalResolution", 4718592);
        map.put("width", (short)320);
        return map;
    }

    private Map<String, Object> getChromaParameters() {
        Map<String, Object> map = new HashMap<>();
        map.put("bitDepthChromaMinus8", (byte)-8);
       map.put( "bitDepthLumaMinus8", (byte)-8);
        map.put("chromaFormatWithReserved", (byte)-3);
       map.put( "numOfSequenceParameterSetExt",(byte) 0);
        return map;
    }
    private Map<String, Object> getAvcConfigurationBoxValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("avcLevelIndication", (byte)13);
        map.put("avcProfileIndication", (byte)100);
        map.put("configurationVersion", (byte)1);
        map.put("numOfPictureParameterSets", (byte)1);
        map.put("profileCompatibility", (byte)0);
        map.put("reservedAndLengthMinusOne", (byte)-1);
        map.put("reservedAndNumOfSequenceParameterSets", (byte)-31);
        return map;
    }

    private Map<String, Object> getSequenceParametersValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("sequenceParameterSetLength", (short)31);
        map.put("sequenceParameterSetNALUnit", new byte []{103, 100, 0, 13, -84, -39, 65, 65, -5, -1, 1, 38, 1, 85, 106,
                2, 2, 2, -128, 0, 0, 3, 0, -128, 0, 0, 30, 7, -118, 20, -53});
        return map;
    }

    private Map<String, Object> getPictureParametersValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("pictureParameterSetLength", (short)6);
        map.put("pictureParameterSetNALUnit", new byte []{104, -21, -29, -53, 34, -64});
        return map;
    }

    private static Object getFirstArrayElement(Object box, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field sampleEntries = box.getClass().getDeclaredField(fieldName);
        sampleEntries.setAccessible(true);
        Object arrayObject = sampleEntries.get(box);
        return Array.get(arrayObject, 0);
    }

    private Object findField(Object rootObject, String... fieldPath) throws NoSuchFieldException, IllegalAccessException {
        Object currentBox = rootObject;
        for (String field : fieldPath) {
            Field field1 = currentBox.getClass().getDeclaredField(field);
            field1.setAccessible(true);
            currentBox = field1.get(currentBox);
        }
        return currentBox;
    }

    private Map<String, Object> getDataReferenceBoxValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("entryCount", 1);
        map.put("flags", new byte []{0, 0, 0});
        map.put("version", (byte)0);
        return map;
    }
    private Map<String, Object> getVideoMediaHeaderBox() {
        Map<String, Object> map = new HashMap<>();
        map.put("flags", new byte []{0, 0, 1});
        map.put("graphicsMode", (short)0);
        map.put("opColor", new short [] {0,0,0});
        map.put("version", (byte)0);
        return map;
    }

    private Map<String, Object> getHandlerBoxExpectedValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("flags", new byte [] {0, 0, 0});
        map.put("handlerType", "vide");
        map.put("name", new byte []{86, 105, 100, 101, 111, 72, 97, 110, 100, 108, 101, 114, 0});
        map.put("predefined", 0);
        map.put("reserved", new int [] {0, 0, 0});
        map.put("version", (byte)0);
        return map;
    }

    private Map<String, Object> getMediaHeaderBoxFields() {
        Map<String, Object> map = new HashMap<>();
        map.put("creationTime", 0);
        map.put("duration", 35328);
        map.put("flags", new byte []{0 ,0 ,0});
        map.put("languageWithPadding", (short)21956);
        map.put("modificationTime", 0);
        map.put("predefined", (short)0);
        map.put("timeScale", 15360);
        map.put("version", (byte) 0);
        return map;
    }

    private Map<String, Object> getTrackHeaderBoxExpectedValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("alternateGroup", (short)0);
        map.put("creationTime", 0);
        map.put("duration", 2300);
        map.put("flags", new byte [] {0, 0, 3});
        map.put("height", 15728640);
        map.put("layer", (short)0);
        map.put("matrix", new int [] {65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824});
        map.put("modificationTime", 0);
        map.put("reserved", 0);
        map.put("reserved2", new int [] {0, 0});
        map.put("reserved3", (short) 0);
        map.put("trackId", 1);
        map.put("version", (byte)0);
        map.put("volume", (short)0);
        map.put("width", 18081017);
        return map;
    }

    private Map<String, Object> getEditListBoxExpectedValues() {
        Map<String, Object> map = new HashMap<>();
        map.put("entryCount", 1);
        map.put("flags", new byte [] {0, 0, 0});
        map.put("mediaRateFraction", new short [] {0});
        map.put("mediaRateInteger", new short [] {1});
        map.put("mediaTimes", new Number [] {1024});
        map.put("segmentDurations", new Number [] {2300});
        map.put("version",(byte) 0);
        return map;
    }

    private Map<String, Object> getFileTypeBoxFields() {
        Map<String, Object> map = new HashMap<>();
        map.put("compatibleBrands", new String [] {"isom", "iso2", "avc1", "mp41"});
        map.put("majorBrand", "isom");
        map.put("minorVersion", 512);
        return map;
    }

    private void assertFields(Object basicBox, Map<String, Object> expectedValues) {
        for (Map.Entry<String, Object> fieldToValue : expectedValues.entrySet()) {
            Assertions.assertThat(basicBox).extracting(fieldToValue.getKey()).isEqualTo(fieldToValue.getValue());
        }
    }

    private Map<String, Object> getMovieHeaderBoxExpectedValues (){
        Map<String, Object> map = new HashMap<>();
        map.put("creationTime", 0);
        map.put("duration", 2300);
        map.put("flags", new byte[]{0, 0, 0});
        map.put("matrix", new int [] {65536, 0, 0, 0, 65536, 0, 0, 0, 1073741824});
        map.put("modificationTime", 0);
        map.put("nextTrackId", 2);
        map.put("preDefined", new int []{0, 0, 0, 0, 0, 0});
        map.put("rate", 65536);
        map.put("reserved", (short) 0);
        map.put("reserved2", new int [] {0, 0});
        map.put("timeScale", 1000);
        map.put("version", (byte)0);
        map.put("volume", (short)256);
        return map;
    }

}