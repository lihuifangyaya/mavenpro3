package tedu;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterMethod;
import static org.testng.Assert.*;
public class NewTest {
	WebDriver driver;
  @Test
  public void f() {
	  System.out.println("11");
	  assertTrue(true);
	 
	 //自定义显示等待，等待结果元素出现
//	 WebElement e = (new WebDriverWait( driver, 10,2000)) .
//			 until(new ExpectedCondition<WebElement>(){
//				 @Override
//				 public WebElement apply( WebDriver d) {
//					 return d.findElement( By.id("xxx"));}});
	 
  }
  @BeforeMethod
  public void beforeMethod() {
  }

  @AfterMethod
  public void afterMethod() {
  }

}
