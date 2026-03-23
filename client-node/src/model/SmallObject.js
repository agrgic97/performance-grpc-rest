'use strict';

class SmallObject {
    constructor(data) {
        for (let i = 1; i <= 65; i++) this[`int_val_${i}`]  = data[`int_val_${i}`];
        for (let i = 1; i <= 15; i++) this[`str_val_${i}`]  = data[`str_val_${i}`];
        for (let i = 1; i <= 10; i++) this[`enum_val_${i}`] = data[`enum_val_${i}`];
        for (let i = 1; i <= 7;  i++) this[`int_arr_${i}`]  = data[`int_arr_${i}`];
        this.str_arr_1  = data.str_arr_1;
        this.str_arr_2  = data.str_arr_2;
        this.enum_arr_1 = data.enum_arr_1;
    }
}

module.exports = { SmallObject };
