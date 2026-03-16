class PayloadAssetService {
    constructor() {
        this.smallObject = null;
        this.mediumObject = null;
        this.largeObject = null;
    }

    load() {
        this.smallObject = this.#generateSmallObject();
        this.mediumObject = this.#generateMediumObject();
        this.largeObject = this.#generateLargeObject();

        return this;
    }

    getSmallObject() {
        return this.smallObject;
    }

    getMediumObject() {
        return this.mediumObject;
    }

    getLargeObject() {
        return this.largeObject;
    }

    #generateSmallObject() {
        const enumCycle = ["OK", "WARN", "ERROR", "PENDING", "UNKNOWN"];

        return {
            int_val_1:  100001, int_val_2:  100002, int_val_3:  100003, int_val_4:  100004, int_val_5:  100005,
            int_val_6:  100006, int_val_7:  100007, int_val_8:  100008, int_val_9:  100009, int_val_10: 100010,
            int_val_11: 100011, int_val_12: 100012, int_val_13: 100013, int_val_14: 100014, int_val_15: 100015,
            int_val_16: 100016, int_val_17: 100017, int_val_18: 100018, int_val_19: 100019, int_val_20: 100020,
            int_val_21: 100021, int_val_22: 100022, int_val_23: 100023, int_val_24: 100024, int_val_25: 100025,
            int_val_26: 100026, int_val_27: 100027, int_val_28: 100028, int_val_29: 100029, int_val_30: 100030,
            int_val_31: 100031, int_val_32: 100032, int_val_33: 100033, int_val_34: 100034, int_val_35: 100035,
            int_val_36: 100036, int_val_37: 100037, int_val_38: 100038, int_val_39: 100039, int_val_40: 100040,
            int_val_41: 100041, int_val_42: 100042, int_val_43: 100043, int_val_44: 100044, int_val_45: 100045,
            int_val_46: 100046, int_val_47: 100047, int_val_48: 100048, int_val_49: 100049, int_val_50: 100050,
            int_val_51: 100051, int_val_52: 100052, int_val_53: 100053, int_val_54: 100054, int_val_55: 100055,
            int_val_56: 100056, int_val_57: 100057, int_val_58: 100058, int_val_59: 100059, int_val_60: 100060,
            int_val_61: 100061, int_val_62: 100062, int_val_63: 100063, int_val_64: 100064, int_val_65: 100065,
            str_val_1:  "alpha",  str_val_2:  "beta",   str_val_3:  "gamma", str_val_4:  "delta",  str_val_5:  "epsilon",
            str_val_6:  "zeta",   str_val_7:  "eta",    str_val_8:  "theta", str_val_9:  "iota",   str_val_10: "kappa",
            str_val_11: "lambda", str_val_12: "mu",     str_val_13: "nu",    str_val_14: "xi",     str_val_15: "omicron",
            enum_val_1:  enumCycle[0], enum_val_2:  enumCycle[1], enum_val_3:  enumCycle[2],
            enum_val_4:  enumCycle[3], enum_val_5:  enumCycle[4], enum_val_6:  enumCycle[0],
            enum_val_7:  enumCycle[1], enum_val_8:  enumCycle[2], enum_val_9:  enumCycle[3],
            enum_val_10: enumCycle[4],
            int_arr_1: [1, 2, 3],   int_arr_2: [4, 5, 6],   int_arr_3: [7, 8, 9],
            int_arr_4: [10, 11, 12], int_arr_5: [13, 14, 15], int_arr_6: [16, 17, 18], int_arr_7: [19, 20, 21],
            str_arr_1: ["foo", "bar", "baz"],
            str_arr_2: ["qux", "quux", "corge"],
            enum_arr_1: ["OK", "WARN", "ERROR"],
        };
    }

    #generateMediumObject() {
        const small = this.#generateSmallObject();
        const items = [];
        for (let i = 0; i < 100; i++) {
            items.push(small);
        }
        return { items };
    }

    #generateLargeObject() {
        const small = this.#generateSmallObject();
        const items = [];
        for (let i = 0; i < 1000; i++) {
            items.push(small);
        }
        return { items };
    }
}

module.exports = {PayloadAssetService};
