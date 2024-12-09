package com.example.coupon.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * UniqueIdGenerator 클래스는 16자리의 영문자와 숫자로 구성된 고유 ID를 생성합니다.
 * 이 클래스는 정적 메서드를 통해 사용 가능합니다.
 */
public class UniqueIdGenerator {

    // SecureRandom 인스턴스는 한 번만 생성하여 재사용합니다.
    private static final SecureRandom RANDOM = new SecureRandom();

    /**
     * 16자리의 고유한 ID를 생성합니다.
     *
     * @return 영문자와 숫자로 구성된 16자리의 문자열 ID
     */
    public static String generateId() {
        // 1. 현재 시간 밀리초 단위로 가져오기
        long timestamp = System.currentTimeMillis();

        // 2. 시간 스탬프를 42비트로 제한 (약 139년의 기간 커버)
        long timePart = timestamp & 0x3FFFFFFFFFFL; // 42비트 마스킹

        // 3. 랜덤 40비트 숫자 생성
        long randomPart = RANDOM.nextLong() & 0xFFFFFFFFFFL; // 40비트 마스킹

        // 4. 비트 결합하여 82비트 숫자 생성
        BigInteger timePartBig = BigInteger.valueOf(timePart).shiftLeft(40); // 시간 부분을 상위 비트로 이동
        BigInteger randomPartBig = BigInteger.valueOf(randomPart);
        BigInteger idNumber = timePartBig.or(randomPartBig); // 시간 부분과 랜덤 부분을 OR 연산으로 결합

        // 5. Base36으로 인코딩하고 16자리로 맞추기
        String idString = idNumber.toString(36).toUpperCase(); // Base36 인코딩 후 대문자로 변환
        idString = String.format("%16s", idString).replace(' ', '0'); // 앞쪽에 0으로 패딩하여 16자리로 맞춤

        return idString;
    }
}