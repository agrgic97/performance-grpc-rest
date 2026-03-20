package grgic.antonio.rest_spring_boot.service;

import grgic.antonio.rest_spring_boot.model.LargeObject;
import grgic.antonio.rest_spring_boot.model.MediumObject;
import grgic.antonio.rest_spring_boot.model.SmallObject;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PayloadAssetService {
    private SmallObject smallObject;
    private MediumObject mediumObject;
    private LargeObject largeObject;

    @PostConstruct
    public void load() {
        this.smallObject = generateSmallObject();
        this.mediumObject = generateMediumObject(this.smallObject);
        this.largeObject = generateLargeObject(this.smallObject);
    }

    private SmallObject generateSmallObject() {
        String[] enumCycle = {"OK", "WARN", "ERROR", "PENDING", "UNKNOWN"};

        return SmallObject.builder()
                .intVal1(100001).intVal2(100002).intVal3(100003).intVal4(100004).intVal5(100005)
                .intVal6(100006).intVal7(100007).intVal8(100008).intVal9(100009).intVal10(100010)
                .intVal11(100011).intVal12(100012).intVal13(100013).intVal14(100014).intVal15(100015)
                .intVal16(100016).intVal17(100017).intVal18(100018).intVal19(100019).intVal20(100020)
                .intVal21(100021).intVal22(100022).intVal23(100023).intVal24(100024).intVal25(100025)
                .intVal26(100026).intVal27(100027).intVal28(100028).intVal29(100029).intVal30(100030)
                .intVal31(100031).intVal32(100032).intVal33(100033).intVal34(100034).intVal35(100035)
                .intVal36(100036).intVal37(100037).intVal38(100038).intVal39(100039).intVal40(100040)
                .intVal41(100041).intVal42(100042).intVal43(100043).intVal44(100044).intVal45(100045)
                .intVal46(100046).intVal47(100047).intVal48(100048).intVal49(100049).intVal50(100050)
                .intVal51(100051).intVal52(100052).intVal53(100053).intVal54(100054).intVal55(100055)
                .intVal56(100056).intVal57(100057).intVal58(100058).intVal59(100059).intVal60(100060)
                .intVal61(100061).intVal62(100062).intVal63(100063).intVal64(100064).intVal65(100065)
                .strVal1("alpha").strVal2("beta").strVal3("gamma").strVal4("delta").strVal5("epsilon")
                .strVal6("zeta").strVal7("eta").strVal8("theta").strVal9("iota").strVal10("kappa")
                .strVal11("lambda").strVal12("mu").strVal13("nu").strVal14("xi").strVal15("omicron")
                .enumVal1(enumCycle[0]).enumVal2(enumCycle[1]).enumVal3(enumCycle[2])
                .enumVal4(enumCycle[3]).enumVal5(enumCycle[4]).enumVal6(enumCycle[0])
                .enumVal7(enumCycle[1]).enumVal8(enumCycle[2]).enumVal9(enumCycle[3])
                .enumVal10(enumCycle[4])
                .intArr1(List.of(1, 2, 3)).intArr2(List.of(4, 5, 6)).intArr3(List.of(7, 8, 9))
                .intArr4(List.of(10, 11, 12)).intArr5(List.of(13, 14, 15))
                .intArr6(List.of(16, 17, 18)).intArr7(List.of(19, 20, 21))
                .strArr1(List.of("foo", "bar", "baz"))
                .strArr2(List.of("qux", "quux", "corge"))
                .enumArr1(List.of("OK", "WARN", "ERROR"))
                .build();
    }

    private MediumObject generateMediumObject(SmallObject smallObject) {
        List<SmallObject> items = new ArrayList<>(100);

        for (int i = 0; i < 100; i++) {
            items.add(smallObject);
        }

        return MediumObject.builder().items(items).build();
    }

    private LargeObject generateLargeObject(SmallObject smallObject) {
        List<SmallObject> items = new ArrayList<>(1000);

        for (int i = 0; i < 5000; i++) {
            items.add(smallObject);
        }

        return LargeObject.builder().items(items).build();
    }

    public SmallObject small() {
        return this.smallObject;
    }

    public MediumObject medium() {
        return this.mediumObject;
    }

    public LargeObject large() {
        return this.largeObject;
    }
}
