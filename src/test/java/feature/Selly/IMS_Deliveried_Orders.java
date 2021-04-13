package feature.Selly;

import org.json.simple.JSONObject;
import org.testng.annotations.Test;
import resource.api.Selly.CartAPI;
import resource.api.Selly.OrderAPI;
import resource.api.Selly.UserAPI;
import resource.common.GlobalVariables;
import resource.common.TestBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;

public class IMS_Deliveried_Orders extends TestBase {

    private String sellerToken = null;
    private String adminToken = null;
    private UserAPI userAPI = new UserAPI();
    private CartAPI CartAPI = new CartAPI();
    private OrderAPI OrderAPI = new OrderAPI();
    private JSONObject customer = null;
    private ArrayList orderIDList = null;

    @Test(dataProvider = "getDataForTest", priority = 1, description = "Add multi items into Cart")
    public void TC01(Hashtable<String, String> data) throws IOException {
        try {

            logStep = logStepInfo(logMethod, "Step #1: Get seller's token from Phone Number");
            sellerToken = userAPI.getSellerToken(logStep, GlobalVariables.SellerPhone);

            logStep = logStepInfo(logMethod, "Step #2: Clear all items in Cart");
            CartAPI.clearItemsInCart(logStep, sellerToken);

            logStep = logStepInfo(logMethod, "Step #3: Add multi items into Cart");
            CartAPI.addMultiItemsIntoCart(logStep, sellerToken, data);

            logStep = logStepInfo(logMethod, "Step #4: Create new Customer");
            customer = OrderAPI.createCustomer(logStep, sellerToken);

            logStep = logStepInfo(logMethod, "Step #5: Create Multiple Order");
            orderIDList = OrderAPI.createMultiOrder(logStep, sellerToken, customer);

            logStep = logStepInfo(logMethod, "Step #6: Get SELLY Admin Token");
            adminToken = userAPI.getAdminToken(logStep, GlobalVariables.SellyAdminID);

            logStep = logStepInfo(logMethod, "Step #7: Selly Admin approves orders");
            OrderAPI.adminApproveOrder(logStep, adminToken, orderIDList);

            logStep = logStepInfo(logMethod, "Step #8: IMS confirmed orders");

            logStep = logStepInfo(logMethod, "Step #9: IMS picking orders");

            logStep = logStepInfo(logMethod, "Step #10: IMS picked orders");

            logStep = logStepInfo(logMethod, "Step #11: IMS delivering orders");

            logStep = logStepInfo(logMethod, "Step #12: IMS delivered orders");


        } catch (Exception e) {
            log4j.error(getStackTrade(e.getStackTrace())) ;
            logException(logMethod, testCaseName, e);
        }

    }

}
