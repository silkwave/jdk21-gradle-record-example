package com.example;

import java.util.Map;

// 'Person'이라는 이름의 레코드 클래스를 정의합니다.
// 레코드는 불변(immutable) 데이터를 위한 간결한 클래스 선언을 제공합니다.
// 자동으로 final 필드, 접근자(accessor) 메서드, equals(), hashCode(), toString() 메서드를 생성합니다.
record Person(String name, int age) {
    // 레코드에 컴팩트 생성자를 추가할 수 있습니다.
    // 이는 암시적 생성자의 본문(body)에 코드를 추가하는 것과 같습니다.
    // 주로 유효성 검사(validation)에 사용됩니다.
    public Person {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        // 필드 할당은 자동으로 이루어집니다. 'this.name = name;' 등은 필요 없습니다.
    }

    // 레코드에 추가적인 인스턴스 메서드를 정의할 수 있습니다.
    public String getDescription() {
        return "이름: " + name + ", 나이: " + age;
    }

    // static 메서드도 추가할 수 있습니다.
    public static void printRecordInfo() {
        System.out.println("Java Records는 데이터를 위한 간결한 불변 클래스를 정의하는 데 유용합니다.");
    }
}

public class RecordExample {
    public static void main(String[] args) {
        // Person 레코드 인스턴스를 생성합니다.
        Person person1 = new Person("홍길동", 30);
        Person person2 = new Person("김철수", 25);

        // 레코드 필드에 접근합니다 (자동 생성된 접근자 메서드 사용).
        System.out.println("Person 1 이름: " + person1.name()); // name() 메서드
        System.out.println("Person 1 나이: " + person1.age());   // age() 메서드

        // toString() 메서드는 자동으로 생성됩니다.
        System.out.println("Person 1 정보: " + person1);

        // equals() 메서드는 자동으로 생성되어 값 기반 비교를 수행합니다.
        Person person3 = new Person("홍길동", 30);
        System.out.println("person1과 person3은 같은가? " + person1.equals(person3)); // true

        // 추가 메서드 호출
        System.out.println(person2.getDescription());

        // static 메서드 호출
        Person.printRecordInfo();

        // --- 추가된 예제 시작 ---

        System.out.println("\n--- 추가 Person 예제 ---");

        // 새로운 Person 인스턴스 생성
        Person person4 = new Person("박영희", 28);
        System.out.println("Person 4 정보: " + person4);
        System.out.println(person4.getDescription());

        // 서로 다른 두 Person 객체 비교
        System.out.println("person1과 person2는 같은가? " + person1.equals(person2)); // false

        // 레코드는 불변 객체입니다. 필드를 변경하려는 시도는 컴파일 오류를 발생시킵니다.
        // 예를 들어, person1.age = 31; 과 같은 코드는 허용되지 않습니다.
        // System.out.println("레코드는 불변 객체입니다. (주석 해제 시 컴파일 오류)");

        // --- 추가된 예제 끝 ---

        // 컴팩트 생성자 유효성 검사 테스트 (주석을 해제하여 테스트 가능)
        try {
            // Person invalidPerson = new Person("잘못된 나이", -5);
            // System.out.println(invalidPerson);
        } catch (IllegalArgumentException e) {
            System.err.println("에러 발생: " + e.getMessage());
        }

        // --- ctxMap 예제 시작 ---
        System.out.println("\n--- ctxMap (컨텍스트 맵) 예제 ---");

        // 애플리케이션의 특정 컨텍스트 정보를 저장할 맵을 생성합니다.
        // 여기서는 불변(immutable) 맵을 사용합니다.
        Map<String, Object> ctxMap = Map.of(
            "applicationName", "RecordExampleApp",
            "version", "1.0.0",
            "currentUser", person1.name(),
            "transactionId", "TXN12345"
        );

        // ctxMap에서 정보를 조회하고 출력합니다.
        System.out.println("컨텍스트 맵 정보:");
        System.out.println("  애플리케이션 이름: " + ctxMap.get("applicationName"));
        System.out.println("  버전: " + ctxMap.get("version"));
        System.out.println("  현재 사용자: " + ctxMap.get("currentUser"));
        System.out.println("  트랜잭션 ID: " + ctxMap.get("transactionId"));

        // 존재하지 않는 키를 조회하면 null이 반환됩니다.
        System.out.println("  존재하지 않는 키 (예: 'sessionId'): " + ctxMap.get("sessionId"));

        // 맵의 모든 항목을 순회하며 출력할 수도 있습니다.
        System.out.println("\n  컨텍스트 맵의 모든 항목:");
        ctxMap.forEach((key, value) -> System.out.println("    " + key + ": " + value));

        // --- ctxMap 예제 끝 ---
    }
}
