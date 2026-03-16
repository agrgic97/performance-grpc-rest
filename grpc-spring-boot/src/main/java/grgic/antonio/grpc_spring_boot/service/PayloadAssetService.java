package grgic.antonio.grpc_spring_boot.service;

import grgic.antonio.grpc_codegen.proto.LargePayload;
import grgic.antonio.grpc_codegen.proto.MediumPayload;
import grgic.antonio.grpc_codegen.proto.SmallPayload;
import grgic.antonio.grpc_codegen.proto.StatusCode;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class PayloadAssetService {
    private SmallPayload smallPayload;
    private MediumPayload mediumPayload;
    private LargePayload largePayload;

    @PostConstruct
    public void load() {
        this.smallPayload = generateSmallObject();
        this.mediumPayload = generateMediumObject();
        this.largePayload = generateLargeObject();
    }

    public SmallPayload smallObject() {
        return this.smallPayload;
    }

    public MediumPayload mediumObject() {
        return this.mediumPayload;
    }

    public LargePayload largeObject() {
        return this.largePayload;
    }

    private SmallPayload generateSmallObject() {
        StatusCode[] enumCycle = {StatusCode.OK, StatusCode.WARN, StatusCode.ERROR, StatusCode.PENDING, StatusCode.UNKNOWN};

        SmallPayload.Builder b = SmallPayload.newBuilder();

        b.setIntVal1(100001).setIntVal2(100002).setIntVal3(100003).setIntVal4(100004).setIntVal5(100005);
        b.setIntVal6(100006).setIntVal7(100007).setIntVal8(100008).setIntVal9(100009).setIntVal10(100010);
        b.setIntVal11(100011).setIntVal12(100012).setIntVal13(100013).setIntVal14(100014).setIntVal15(100015);
        b.setIntVal16(100016).setIntVal17(100017).setIntVal18(100018).setIntVal19(100019).setIntVal20(100020);
        b.setIntVal21(100021).setIntVal22(100022).setIntVal23(100023).setIntVal24(100024).setIntVal25(100025);
        b.setIntVal26(100026).setIntVal27(100027).setIntVal28(100028).setIntVal29(100029).setIntVal30(100030);
        b.setIntVal31(100031).setIntVal32(100032).setIntVal33(100033).setIntVal34(100034).setIntVal35(100035);
        b.setIntVal36(100036).setIntVal37(100037).setIntVal38(100038).setIntVal39(100039).setIntVal40(100040);
        b.setIntVal41(100041).setIntVal42(100042).setIntVal43(100043).setIntVal44(100044).setIntVal45(100045);
        b.setIntVal46(100046).setIntVal47(100047).setIntVal48(100048).setIntVal49(100049).setIntVal50(100050);
        b.setIntVal51(100051).setIntVal52(100052).setIntVal53(100053).setIntVal54(100054).setIntVal55(100055);
        b.setIntVal56(100056).setIntVal57(100057).setIntVal58(100058).setIntVal59(100059).setIntVal60(100060);
        b.setIntVal61(100061).setIntVal62(100062).setIntVal63(100063).setIntVal64(100064).setIntVal65(100065);

        b.setStrVal1("alpha").setStrVal2("beta").setStrVal3("gamma").setStrVal4("delta").setStrVal5("epsilon");
        b.setStrVal6("zeta").setStrVal7("eta").setStrVal8("theta").setStrVal9("iota").setStrVal10("kappa");
        b.setStrVal11("lambda").setStrVal12("mu").setStrVal13("nu").setStrVal14("xi").setStrVal15("omicron");

        b.setEnumVal1(enumCycle[0]).setEnumVal2(enumCycle[1]).setEnumVal3(enumCycle[2]);
        b.setEnumVal4(enumCycle[3]).setEnumVal5(enumCycle[4]).setEnumVal6(enumCycle[0]);
        b.setEnumVal7(enumCycle[1]).setEnumVal8(enumCycle[2]).setEnumVal9(enumCycle[3]);
        b.setEnumVal10(enumCycle[4]);

        b.addIntArr1(1).addIntArr1(2).addIntArr1(3);
        b.addIntArr2(4).addIntArr2(5).addIntArr2(6);
        b.addIntArr3(7).addIntArr3(8).addIntArr3(9);
        b.addIntArr4(10).addIntArr4(11).addIntArr4(12);
        b.addIntArr5(13).addIntArr5(14).addIntArr5(15);
        b.addIntArr6(16).addIntArr6(17).addIntArr6(18);
        b.addIntArr7(19).addIntArr7(20).addIntArr7(21);

        b.addStrArr1("foo").addStrArr1("bar").addStrArr1("baz");
        b.addStrArr2("qux").addStrArr2("quux").addStrArr2("corge");

        b.addEnumArr1(StatusCode.OK).addEnumArr1(StatusCode.WARN).addEnumArr1(StatusCode.ERROR);

        return b.build();
    }

    private MediumPayload generateMediumObject() {
        MediumPayload.Builder builder = MediumPayload.newBuilder();
        SmallPayload small = generateSmallObject();

        for (int i = 0; i < 100; i++) {
            builder.addItems(small);
        }

        return builder.build();
    }

    private LargePayload generateLargeObject() {
        LargePayload.Builder builder = LargePayload.newBuilder();
        SmallPayload small = generateSmallObject();

        for (int i = 0; i < 1000; i++) {
            builder.addItems(small);
        }

        return builder.build();
    }
}
