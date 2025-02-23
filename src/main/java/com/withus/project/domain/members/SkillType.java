package com.withus.project.domain.members;

import lombok.Getter;

@Getter
public enum SkillType {
    PYTHON(101, "Python"),
    JAVASCRIPT(102, "JavaScript"),
    JAVA(103, "Java"),
    C(104, "C"),
    CPP(105, "C++"),
    CSHARP(106, "C#"),
    PHP(107, "PHP"),
    TYPESCRIPT(108, "TypeScript"),
    SWIFT(109, "Swift"),
    KOTLIN(110, "Kotlin"),
    GO(111, "Go"),
    RUBY(112, "Ruby"),
    R(113, "R"),
    DART(114, "Dart"),
    SCALA(115, "Scala"),
    PERL(116, "Perl"),
    OBJECTIVE_C(117, "Objective-C"),
    LUA(118, "Lua"),
    MATLAB(119, "MATLAB"),
    RUST(120, "Rust"),
    REACT(121, "React"),
    ANGULAR(122, "Angular"),
    VUE(123, "Vue.js"),
    DJANGO(124, "Django"),
    FLASK(125, "Flask"),
    SPRING_BOOT(126, "Spring Boot"),
    ASP_NET(127, "ASP.NET"),
    LARAVEL(128, "Laravel"),
    EXPRESS(129, "Express"),
    RUBY_ON_RAILS(130, "Ruby on Rails"),
    JQUERY(131, "jQuery"),
    BOOTSTRAP(132, "Bootstrap"),
    SASS(133, "Sass"),
    LESS(134, "LESS"),
    EMBER_JS(135, "Ember.js"),
    MYSQL(136, "MySQL"),
    POSTGRESQL(137, "PostgreSQL"),
    SQLITE(138, "SQLite"),
    MONGODB(139, "MongoDB"),
    REDIS(140, "Redis"),
    ORACLE(141, "Oracle"),
    MSSQL(142, "Microsoft SQL Server"),
    ELASTICSEARCH(143, "Elasticsearch"),
    FIREBASE(144, "Firebase"),
    CASSANDRA(145, "Cassandra"),
    MARIA_DB(146, "MariaDB"),
    DYNAMO_DB(147, "DynamoDB"),
    COUCH_DB(148, "CouchDB"),
    NEO4J(149, "Neo4j"),
    IBM_DB2(150, "IBM Db2"),
    AWS(151, "AWS"),
    AZURE(152, "Microsoft Azure"),
    GOOGLE_CLOUD_PLATFORM(153, "Google Cloud Platform"),
    DOCKER(154, "Docker"),
    KUBERNETES(155, "Kubernetes"),
    JENKINS(156, "Jenkins"),
    TERRAFORM(157, "Terraform"),
    ANSIBLE(158, "Ansible"),
    TRAVIS_CI(159, "Travis CI"),
    CIRCLE_CI(160, "CircleCI"),
    GITLAB_CI_CD(161, "GitLab CI/CD"),
    PUPPET(162, "Puppet"),
    CHEF(163, "Chef"),
    NAGIOS(164, "Nagios"),
    PROMETHEUS(165, "Prometheus"),
    ANDROID(166, "Android"),
    IOS(167, "iOS"),
    REACT_NATIVE(168, "React Native"),
    FLUTTER(169, "Flutter"),
    XAMARIN(170, "Xamarin"),
    SWIFT_UI(171, "SwiftUI"),
    KOTLIN_MULTIPLATFORM(172, "Kotlin Multiplatform"),
    ELECTRON(173, "Electron"),
    QT(174, "Qt"),
    IONIC(175, "Ionic"),
    CORDOVA(176, "Cordova"),
    PHONEGAP(177, "PhoneGap"),
    UNITY(178, "Unity"),
    UNREAL_ENGINE(179, "Unreal Engine"),
    COCOS2DX(180, "Cocos2dx"),
    GRAPHQL(181, "GraphQL"),
    S3(182, "S3"),
    GCP(183, "GCP"),
    BLE(184, "Bluetooth Low Energy"),
    NGINX(185, "Nginx"),
    APACHE(186, "Apache"),
    TOMCAT(187, "Tomcat"),
    KAFKA(188, "Kafka"),
    ZOOKEEPER(189, "Zookeeper"),
    TABLEAU(190, "Tableau"),
    POWER_BI(191, "Power BI"),
    CLICKHOUSE(192, "ClickHouse"),
    SPARK(193, "Apache Spark"),
    BIGQUERY(194, "BigQuery"),
    DATASTAX(195, "DataStax"),
    SNOWFLAKE(196, "Snowflake"),
    REDSHIFT(197, "Redshift"),
    LOOKER(198, "Looker"),
    AI_PLATFORM(199, "AI Platform"),
    PYTORCH_LIGHTNING(200, "PyTorch Lightning"),
    H2O_AI(201, "H2O.ai"),
    RAPIDMINER(202, "RapidMiner"),
    KNIME(203, "KNIME"),
    PANDAS(204, "Pandas"),
    SCIPY(205, "SciPy"),
    SCIPY_STATS(206, "SciPy Stats"),
    HADOOP(207, "Hadoop"),
    MAHOUT(208, "Mahout"),
    ML_LIB(209, "MLlib"),
    FFMPEG(210, "FFmpeg"),
    OPENCV(211, "OpenCV"),
    LATEX(212, "LaTeX"),
    ILLUSTRATOR(213, "Illustrator"),
    PHOTOSHOP(214, "Photoshop"),
    PREMIERE_PRO(215, "Premiere Pro"),
    BLENDER(216, "Blender"),
    FIGMA(217, "Figma"),
    SKETCH(218, "Sketch"),
    INVISION(219, "InVision"),
    PROTOPIE(220, "ProtoPie"),
    AXURE_RP(221, "Axure RP"),
    JIRA(222, "JIRA"),
    POSTMAN(223, "Postman"),
    SWAGGER(224, "Swagger"),
    PYCHARM(225, "PyCharm"),
    VSCODE(226, "VSCode"),
    INTELLIJ(227, "IntelliJ IDEA"),
    ECLIPSE(228, "Eclipse"),
    NETBEANS(229, "NetBeans"),
    JSP(230, "JSP");

    private final int code;
    private final String name;

    SkillType(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static SkillType fromCode(int code) {
        for (SkillType skill : values()) {
            if (skill.code == code) {
                return skill;
            }
        }
        throw new IllegalArgumentException("Invalid skill code: " + code);
    }

    public static SkillType fromName(String name) {
        for (SkillType skill : values()) {
            if (skill.name().equalsIgnoreCase(name)) {
                return skill;
            }
        }
        throw new IllegalArgumentException("Invalid skill name: " + name);
    }
}
