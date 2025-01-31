package kg.edu.manas.cloud.model.data.constants;

public class Messages {
    /**
     * Http
     */
    public static final String USER_NOT_FOUND = "Колдонуучу табылган жок";
    public static final String EXPIRED_OTP = "Бир жолку сыр создун мооноту буткон";
    public static final String INVALID_OPT = "Бир жолку сыр соз туура эмес";
    public static final String USER_ALREADY_EXIST = "Колдонуучу бул имейл менен катталган";
    public static final String INVALID_TOKEN = "JWT токен туура эмес";
    public static final String EXPIRED_TOKEN = "JWT токендин мооноту буткон";
    public static final String CACHE_CLEARED = "Кэш ийгиликтуу тазаланды";

    /**
     * Mail
     */
    public static final String REGISTRATION_OTP_SUB = "Каттоо учун бир жолку сыр соз: %s";
    public static final String HELP_SUB = "Тез жардам";

    public static final String HELP_MSG = "%s үчүн %s алгылыктуу чектен ашты. Жайгашкан жери: https://2gis.kg/search/geo/%s. Убакыт: %s";
    // https://2gis.kg/search/geo/74.576216479763389,42.832382144897558

    /**
     * Other
     */
    public static final String TOPIC_WARNING_MSG = "Эскертүү: %s нормалдуу диапазондон тышкары. Сураныч, кылдат көзөмөлдөңүз";
    public static final String TOPIC_CRITICAL_MSG = "Критикалык: %s коопсуз параметрлерден тышкары. Дароо көңүл буруу зарыл";

    public static final String HEART_BEAT = "Журоктун согушу";
    public static final String SATURATION = "Сатурация";
    public static final String AIR_QUALITY = "Абанын сапаты";
}
