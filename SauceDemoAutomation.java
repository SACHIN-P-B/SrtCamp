package Pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import io.github.bonigarcia.wdm.WebDriverManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SauceDemoAutomation {
    public static void main(String[] args) throws InterruptedException {
        WebDriverManager.chromedriver().setup();
        WebDriver driver = new ChromeDriver();
        driver.manage().window().maximize();
        driver.get("https://www.saucedemo.com/");

        // === Login to the app ===
        driver.findElement(By.id("user-name")).sendKeys("problem_user");
        driver.findElement(By.id("password")).sendKeys("secret_sauce");
        driver.findElement(By.id("login-button")).click();

        // === Handle password change popup if it appears ===
        try {
            WebElement popupOkButton = driver.findElement(By.xpath("//button[text()='OK' or normalize-space()='Ok']"));
            popupOkButton.click();
            System.out.println("Password change popup handled.");
        } catch (Exception e) {
            System.out.println("No password change popup found. Continuing...");
        }

        // === SCENARIO 1: Verify Z-A Sorting ===
        Select sortDropdown = new Select(driver.findElement(By.className("product_sort_container")));
        sortDropdown.selectByVisibleText("Name (Z to A)");
        Thread.sleep(1000); // Wait for sorting to apply

        List<WebElement> items = driver.findElements(By.className("inventory_item_name"));
        List<String> actualItemNames = new ArrayList<>();
        for (WebElement item : items) {
            actualItemNames.add(item.getText());
        }

        List<String> sortedItemNames = new ArrayList<>(actualItemNames);
        sortedItemNames.sort(Collections.reverseOrder());

        if (actualItemNames.equals(sortedItemNames)) {
            System.out.println("Z to A sorting is correct.");
        } else {
            System.out.println("Z to A sorting failed.");
        }

        // === SCENARIO 2: Verify High to Low Price Sorting ===
        sortDropdown = new Select(driver.findElement(By.className("product_sort_container"))); // Re-locate dropdown
        sortDropdown.selectByVisibleText("Price (high to low)");
        Thread.sleep(1000);

        List<WebElement> prices = driver.findElements(By.className("inventory_item_price"));
        List<Double> actualPrices = new ArrayList<>();
        for (WebElement price : prices) {
            actualPrices.add(Double.parseDouble(price.getText().replace("$", "")));
        }

        List<Double> sortedPrices = new ArrayList<>(actualPrices);
        sortedPrices.sort(Collections.reverseOrder());

        if (actualPrices.equals(sortedPrices)) {
            System.out.println("Price high to low sorting is correct.");
        } else {
            System.out.println("Price high to low sorting failed.");
        }

        // === SCENARIO 3: Add multiple items & Validate Checkout ===
        List<WebElement> addButtons = driver.findElements(By.xpath("//button[text()='Add to cart']"));
        if (addButtons.size() >= 2) {
            addButtons.get(0).click();
            addButtons.get(1).click();
        }

        // Go to cart
        driver.findElement(By.className("shopping_cart_link")).click();
        Thread.sleep(1000);

        // Click Checkout
        driver.findElement(By.id("checkout")).click();

        // Fill customer details
        driver.findElement(By.id("first-name")).sendKeys("John");
        driver.findElement(By.id("last-name")).sendKeys("Doe");
        driver.findElement(By.id("postal-code")).sendKeys("12345");
        driver.findElement(By.id("continue")).click();

        // Validate total price (Items total only)
        List<WebElement> checkoutPrices = driver.findElements(By.className("inventory_item_price"));
        double total = 0.0;
        for (WebElement p : checkoutPrices) {
            total += Double.parseDouble(p.getText().replace("$", ""));
        }

        String displayedTotal = driver.findElement(By.className("summary_subtotal_label")).getText();
        double displayedValue = Double.parseDouble(displayedTotal.replace("Item total: $", ""));

        if (Math.abs(total - displayedValue) < 0.01) {
            System.out.println("Checkout total is correct.");
        } else {
            System.out.println("Checkout total mismatch.");
        }

        // Finish order
        driver.findElement(By.id("finish")).click();

        // Confirm completion
        String confirmation = driver.findElement(By.className("complete-header")).getText();
        if (confirmation.equals("Thank you for your order!")) {
            System.out.println("Checkout completed successfully.");
        } else {
            System.out.println("Checkout failed.");
        }

        // Close the browser
       // driver.quit();
    }
}
