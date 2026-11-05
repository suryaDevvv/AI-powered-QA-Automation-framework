package tests.support;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class ApiRetryAnalyzer implements IRetryAnalyzer {
    private int retryCount;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < 1) {
            retryCount++;
            return true;
        }
        return false;
    }
}
