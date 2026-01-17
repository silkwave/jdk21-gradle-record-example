package com.example;

import util.CtxMap; // util.CtxMap 클래스 임포트
import util.MapUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map; // Map 사용을 위해 추가
import java.util.Optional; // Optional 사용을 위해 추가
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

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

    // Setter 대신 사용하는 Wither 메서드: 값을 변경한 새로운 인스턴스를 반환
    public Person withName(String name) {
        return new Person(name, this.age);
    }
}

// CtxMap에 저장된 설정 데이터를 표현하는 레코드 정의
record AppContext(
        String applicationName,
        String version,
        String currentUser,
        String transactionId,
        int timeoutSeconds,
        boolean debugMode,
        double rateLimit) {
}

public class RecordExample {
    public static void main(String[] args) {
        // [리팩토링] 전역 컨텍스트(CtxMap) 생성 및 전달
        CtxMap context = new CtxMap();

        demonstrateRecordFeatures(context);
        demonstrateCtxMapFeatures(context);
        demonstrateLinkedBlockingQueue(context);
        demonstrateMapUtils(context);
    }

    /**
     * Java Record의 기본 기능(생성, 조회, 불변성, Wither 등)을 시연합니다.
     */
    private static void demonstrateRecordFeatures(CtxMap ctx) {
        // Person 레코드 인스턴스를 생성합니다.
        Person person1 = new Person("홍길동", 30);
        Person person2 = new Person("김철수", 25);

        // 레코드 필드에 접근합니다 (자동 생성된 접근자 메서드 사용).
        System.out.println("Person 1 이름: " + person1.name()); // name() 메서드
        System.out.println("Person 1 나이: " + person1.age()); // age() 메서드

        // person1.name("1111"); // 컴파일 에러: 레코드는 불변이며 name()은 인자를 받지 않는 접근자입니다.

        // [Get 예제] 자동 생성된 접근자(Getter) 사용
        System.out.println("Get 이름: " + person1.name());

        // [Set 예제] Wither 메서드를 사용하여 값 변경 (새 객체 생성)
        Person person1New = person1.withName("1111");
        System.out.println("Set 이름 결과 (새 객체): " + person1New);

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
    }

    /**
     * CtxMap 유틸리티 클래스의 활용 방법을 시연합니다.
     */
    private static void demonstrateCtxMapFeatures(CtxMap ctx) {
        System.out.println("\n--- CtxMap (컨텍스트 맵) 활용 예제 ---");

        // 1. 초기 데이터 구성
        populateInitialData(ctx);

        // 2. Record 변환 및 활용
        demonstrateRecordConversion(ctx);

        // 3. 복잡한 데이터 추가 (List, Map 등)
        addComplexDataTo(ctx);

        // 4. 데이터 조회 기능 검증
        verifyDataRetrieval(ctx);

        // 5. 전체 출력
        printAllEntries(ctx);

        // 6. 비즈니스 로직 연동
        handlePrivateLogic(ctx);
    }

    private static void populateInitialData(CtxMap ctx) {
        Person person = new Person("홍길동", 30);
        ctx
                .put("applicationName", "RecordExampleApp_V2")
                .put("version", "1.0.1")
                .put("currentUser", person.name())
                .put("transactionId", "TXN12345")
                .put("timeoutSeconds", 30) // int 값
                .put("debugMode", true) // boolean 값
                .put("rateLimit", 10.5); // double 값
    }

    private static void demonstrateRecordConversion(CtxMap ctx) {
        AppContext appContext = new AppContext(
                ctx.getString("applicationName"),
                ctx.getString("version"),
                ctx.getString("currentUser"),
                ctx.getString("transactionId"),
                ctx.getInt("timeoutSeconds"),
                ctx.getBoolean("debugMode"),
                ctx.getDouble("rateLimit"));
        System.out.println("CtxMap으로 생성한 AppContext 레코드: " + appContext);

        processAppContext(appContext);
    }

    private static void addComplexDataTo(CtxMap ctx) {
        // Map을 사용하여 CtxMap 초기화
        HashMap<String, Object> additionalData = new HashMap<>();
        additionalData.put("source", "API_Gateway");
        additionalData.put("requestCount", 100L); // long 값
        ctx.putAll(additionalData);

        // List<String> 예제 추가
        List<String> featureFlags = List.of("FEATURE_A", "FEATURE_B", "FEATURE_C");
        ctx.put("featureFlags", featureFlags);

        // Map<String, Object> 예제 추가
        CtxMap userInfo = new CtxMap()
                .put("id", "user_001")
                .put("roles", List.of("ADMIN", "USER"));
        ctx.put("userInfo", userInfo.asReadOnlyMap()); // 내부 Map은 읽기 전용으로 저장
    }

    private static void verifyDataRetrieval(CtxMap ctx) {
        // CtxMap에서 타입-안전하게 정보 조회
        System.out.println("CtxMap 정보:");
        System.out.println("  애플리케이션 이름 (String): " + ctx.getString("applicationName"));
        System.out.println("  버전 (String, 기본값): " + ctx.getString("version", "N/A"));
        System.out.println("  현재 사용자 (String): " + ctx.getString("currentUser"));
        System.out.println("  트랜잭션 ID (String): " + ctx.getString("transactionId"));
        System.out.println("  타임아웃 (int): " + ctx.getInt("timeoutSeconds"));
        System.out.println("  디버그 모드 (boolean): " + ctx.getBoolean("debugMode"));
        System.out.println("  속도 제한 (double): " + ctx.getDouble("rateLimit"));
        System.out.println("  소스 (String): " + ctx.getString("source"));
        System.out.println("  요청 수 (long): " + ctx.getLong("requestCount"));

        // 존재하지 않는 키 조회 시 기본값 활용
        System.out.println("  존재하지 않는 String 키 (기본값 제공): " + ctx.getString("nonExistentKey", "기본값"));
        System.out.println("  존재하지 않는 int 키 (기본값 제공): " + ctx.getInt("nonExistentInt", 999));

        // Optional을 사용한 조회 예제 (NPE 방지)
        Optional<String> optionalUser = ctx.getOptional("currentUser", String.class);
        optionalUser.ifPresent(u -> System.out.println("  Optional 사용자: " + u));

        Optional<Integer> optionalInvalidInt = ctx.getOptional("invalidIntKey", Integer.class);
        System.out.println("  Optional 존재하지 않는 int (isPresent): " + optionalInvalidInt.isPresent());

        // getObject로 특정 타입 조회
        Integer timeout = ctx.getObject("timeoutSeconds", Integer.class);
        System.out.println("  getObject로 조회한 타임아웃 (Integer): " + timeout);

        // getList로 리스트 조회
        List<String> flags = ctx.getList("featureFlags", String.class);
        System.out.println("  기능 플래그 (List<String>): " + flags);
        List<Integer> emptyList = ctx.getList("nonExistentList", Integer.class);
        System.out.println("  존재하지 않는 리스트 (null): " + emptyList);

        // getMap으로 맵 조회
        Map<String, Object> userMap = ctx.getMap("userInfo"); // CtxMap의 getMap은 Map<String, Object>를 반환
        if (userMap != null) {
            System.out.println("  사용자 정보 맵 (ID): " + userMap.get("id"));
            System.out.println("  사용자 정보 맵 (Roles): " + userMap.get("roles"));
        }

        // hasText 및 containsKey 예제
        System.out.println("  'applicationName' 키 존재 여부: " + ctx.containsKey("applicationName"));
        System.out.println("  'applicationName' 텍스트 존재 여부: " + ctx.hasText("applicationName"));
        System.out.println("  'emptyString' 텍스트 존재 여부 (없음): " + ctx.hasText("emptyString"));
        ctx.put("emptyString", "");
        System.out.println("  'emptyString' 텍스트 존재 여부 (빈 문자열): " + ctx.hasText("emptyString"));
        ctx.put("blankString", "   ");
        System.out.println("  'blankString' 텍스트 존재 여부 (공백 문자열): " + ctx.hasText("blankString"));
    }

    private static void printAllEntries(CtxMap ctx) {
        System.out.println("\n  CtxMap 전체 목록:");
        ctx.asReadOnlyMap().forEach((key, value) -> System.out.println("    * " + key + " = " + value));
    }

    /**
     * CtxMap을 전달받아 처리하는 private 메서드입니다.
     * 외부로 노출되지 않는 내부 비즈니스 로직을 캡슐화할 때 사용합니다.
     */
    private static void handlePrivateLogic(CtxMap ctx) {
        System.out.println("\n--- [Private Method] 내부 로직 처리 ---");

        // CtxMap에서 데이터 조회
        String user = ctx.getString("currentUser");
        boolean debug = ctx.getBoolean("debugMode");

        System.out.println("  내부 로직에서 사용자 확인: " + user);

        if (debug) {
            System.out.println("  [보안 로그] 디버그 모드로 인해 상세 정보를 기록합니다.");
            // 필요한 경우 여기서 추가적인 민감한 처리를 수행
        }
    }

    /**
     * AppContext 레코드를 전달받아 처리하는 메서드입니다.
     * Map 대신 명시적인 타입을 사용하므로 컴파일 시점에 타입 체크가 가능합니다.
     */
    private static void processAppContext(AppContext context) {
        System.out.println("\n--- [AppContext 처리] 레코드 기반 로직 ---");
        System.out.println("  앱 이름: " + context.applicationName());
        if (context.debugMode()) {
            System.out.println("  [DEBUG] 트랜잭션 ID: " + context.transactionId());
        }
    }

    /**
     * LinkedBlockingQueue를 사용한 큐 동작 예제입니다.
     * put()은 큐가 꽉 차면 대기하고, take()는 큐가 비면 대기하는 블로킹 동작을 수행합니다.
     */
    private static void demonstrateLinkedBlockingQueue(CtxMap ctx) {
        System.out.println("\n--- LinkedBlockingQueue 활용 예제 ---");

        // 최대 3개의 요소를 담을 수 있는 블로킹 큐 생성
        BlockingQueue<String> queue = new LinkedBlockingQueue<>(3);

        try {
            // 1. 데이터 추가 (put: 공간이 있을 때까지 대기, 여기선 바로 추가됨)
            System.out.println("[Queue] 데이터 추가 중...");
            queue.put("Job_1");
            queue.put("Job_2");
            queue.put("Job_3");
            System.out.println("  현재 큐 상태: " + queue);

            // 2. offer를 사용한 추가 시도 (큐가 꽉 차서 실패함 -> false 반환, 대기하지 않음)
            boolean accepted = queue.offer("Job_4");
            System.out.println("  Job_4 추가 성공 여부 (offer): " + accepted);

            // 3. 데이터 꺼내기 (take: 데이터가 있을 때까지 대기)
            String job = queue.take();
            System.out.println("[Queue] 데이터 처리(take): " + job);
            System.out.println("  처리 후 큐 상태: " + queue);

            // 4. 빈 공간에 다시 추가
            queue.put("Job_5");
            System.out.println("[Queue] Job_5 추가 완료. 최종 상태: " + queue);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.err.println("큐 작업 중 인터럽트 발생");
        }
    }

    /**
     * MapUtils를 사용하여 객체(Record)를 Map으로 변환하는 예제입니다.
     */
    private static void demonstrateMapUtils(CtxMap globalCtx) {
        System.out.println("\n--- MapUtils (Jackson) 활용 예제 ---");

        // 변환할 레코드 객체 생성
        Person person = new Person("Jang", 45);

        // MapUtils.toMap()을 사용하여 Record -> Map<String, Object> 변환
        Map<String, Object> personMap = MapUtils.toMap(person);

        System.out.println("원본 Record: " + person);
        System.out.println("변환된 Map: " + personMap);

        // 변환된 Map을 CtxMap으로 감싸서 활용 가능
        CtxMap ctx = CtxMap.of(personMap);
        System.out.println("CtxMap에서 이름 조회: " + ctx.getString("name"));

        // [리팩토링] 변환된 CtxMap을 private 비즈니스 로직 메서드에 전달
        // handlePrivateLogic 메서드가 필요로 하는 데이터('currentUser')를 매핑해줍니다.
        ctx.put("currentUser", ctx.getString("name"))
           .put("debugMode", true);
        handlePrivateLogic(ctx);
    }
}
