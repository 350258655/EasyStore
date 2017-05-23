package com.shake.easystore;

/**
 * Created by shake on 17-5-4.
 */
public class Contants {


    public static final String  COMPAINGAIN_ID="compaigin_id";
    public static final String  WARE="ware";

    public  static final String DES_KEY="Cniao5_123456";
    public static final String USER_JSON="user_json";
    public static final String TOKEN="token";
    public static final String ADDRESS="address";
    public static final String BUNDLE_ADDRESS="address";
    public static final String BUNDLE_CHECK_CART="check_cart";
    public static final String CHECK_CART="isCheck";

    public  static final int REQUEST_CODE=0;
    public  static final int REQUEST_EDIT_CODE=1;

    public static final int RESULT_SUCCESS = 200;
    public static final int RESULT_CANCLE= 300;
    public static final int RESULT_FAILD= 400;
    public static final int REQUEST_ALIPAY= 3;
    public static final int REQUEST_WXPAY= 4;



    //
    public static class API{

        public static final String BASE_URL="http://112.124.22.238:8081/course_api/";

        public static final String CAMPAIGN_HOME=BASE_URL + "campaign/recommend";

        public static final String WARES_HOT=BASE_URL + "wares/hot";

        public static final String BANNER=BASE_URL +"banner/query";

        public static final String WARES_LIST=BASE_URL +"wares/list";

        public static final String CATEGORY_LIST=BASE_URL +"category/list";
        public static final String WARES_CAMPAIN_LIST=BASE_URL +"wares/campaign/list";
        public static final String WARES_DETAIL=BASE_URL +"wares/detail.html";
        public static final String LOGIN=BASE_URL +"auth/login";
        public static final String USER_DETAIL=BASE_URL +"user/get?id=1";
        public static final String REG=BASE_URL +"auth/reg";

        public static final String ADDRESS_LIST=BASE_URL +"addr/list";
        public static final String ADDRESS_CREATE=BASE_URL +"addr/create";
        public static final String ADDRESS_UPDATE=BASE_URL +"addr/update";


        public static final String HD_LOGIN = "http://api.hdg123.cn/index.php?r=auth/authorize&game_id=TEST-DEMO&package_id=T5&s=VXS%2FAMTCla6NBJwBNtaR7X4En4sJJ6pnBns4klee2On7PpH73G%2FSRpwZd0qD+8nl4MsQSaojGwb%2FN0M8zJNuJ0SkaUFp4auaRxkGu2%2BV%2BQZqvUQnR0JtGn7cb+RM2WAsTjLHX88W3IN71XpGZiy5vKlnEXpvNQJbvEKDNZ92UXWQ8y61OSH%2BKI+%2Bloxr2mb%2BBlxRPGpj2XR4v2upfRMIy8aRB8b0mJ%2BKOO2rdEYHey4fKioY6oW+X35lVfFS35ytGJHf%2F86i53MvAnz76n%2BBmoYGHNuNk2wKb6jHXRpfzW%2FxBFiA+6lZQKfq3s9xO%2Bi3jW9ODrR3W3DVmas%2BrhcqQcVQ%2FFkHKV3Ql7%2BPJ6nipmN3f+goEF1qXITvv31IB02s0F4W6uuoOZbMhb8S5WsMk5gVoGK9AcJP6rJY6dhhvX+m0PGoiRnfy0pXwTYGI095rkIx%2FujtnmGhGiEK2748qUnYFXwtOrhUBD2Tr7Y+gG4q%2B4CrjfCXfzRR%2BIh5StsqdDLgQmeRwx4Vot5ltYta9WB92ehTccbKBqzM+%2F%2FWRZqjwWj%2BEDXoMZdvXuskgSQ%2B8Jfh670GZFaFL9%2FrX56HrUDXM%2BWTKY6DE+DYrz%2BC2b%2B3RECPpG2SyzSdCraIN0gGKx6QDNO7ETh2qEv3j238g3evEXp6LK+RpPPjznKXlUvcOPngvhGB%2FJcX3qufQlHtnEUm%2Bfw%2BtJc1LoHQ%2FmKLo9qnZX%2B+TfILAYcXYnGNYyDACHaGxE1Pw6gXt5ZiDar8ZvvuN0SiW0iMvlUyLLMjDZuu+M1SLcOsIF4fKw%2BapRDZA&username=D980707&password=440991";
        public static final String HD_CREATE_ORDER = "http://api.hdg123.cn/index.php?r=order/create&s=VXS%2FAMTCla6NBJwBNtaR7X4En4sJJ6pnBns4klee2On7PpH73G%2FSRpwZd0qD+8nl4MsQSaojGwb%2FN0M8zJNuJ0SkaUFp4auaRxkGu2%2BV%2BQZqvUQnR0JtGn7cb+RM2WAsTjLHX88W3IN71XpGZiy5vKlnEXpvNQJbvEKDNZ92UXWQ8y61OSH%2BKI+%2Bloxr2mb%2BBlxRPGpj2XR4v2upfRMIy8aRB8b0mJ%2BKOO2rdEYHey4fKioY6oW+X35lVfFS35ytGJHf%2F86i53MvAnz76n%2BBmoYGHNuNk2wKb6jHXRpfzW%2FxBFiA+6lZQKfq3s9xO%2Bi3jW9ODrR3W3DVmas%2BrhcqQcVQ%2FFkHKV3Ql7%2BPJ6nipmN3f+goEF1qXITvv31IB02s0F4W6uuoOZbMhb8S5WsMk5gVoGK9AcJP6rJY6dhhvX+m0PGoiRnfy0pXwTYGI095rkIx%2FujtnmGhGiEK2748qUnYFXwtOrhUBD2Tr7Y+gG4q%2B4CrjfCXfzRR%2BIh5StsqdDLgQmeRwx4Vot5ltYta9WB92ehTccbKBqzM+%2F%2FWRZqjwWj%2BEDXoMZdvXuskgSQ%2B8Jfh670GZFaFL9%2FrX56HrUDXM%2BWTKY6DE+DYrz%2BC2b%2B3RECPpG2SyzSdCraIN0gGKx6QDNO7ETh2qEv3j238g3evEXp6LK+RpPPjznKXlUvcOPngvhGB%2FJcX3qufQlHtnEUm%2Bfw%2BtJc1LoHQ%2FmKLo9qnZX%2B+TfILAYcXYnGNYyDACHaGxE1Pw6gXt5ZiDar8ZvvuN0SiW0iMvlUyLLMjDZuu+M1SLcOsIF4fKw%2BapRDZA";

    }

}
