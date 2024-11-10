/**
 * @description 다양한 유틸리티 함수를 제공하는 모듈입니다.
 * @author 손의현 (SONY-STRING)
 */


/**
 * @description includes utility methods
 * @class
 */
class utils {
    /**
     * @description console.log() wrapper working when only `dev` build
     * @author 손의현(SONY_STRING)
     * */
    static dlog(msg) {
        if (process.env.NODE_ENV === "development") {
            console.log(msg);
        }
    }
}





export default utils;
