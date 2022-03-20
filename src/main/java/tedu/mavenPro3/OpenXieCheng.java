
package tedu.mavenPro3;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

public class OpenXieCheng {
	ArrayList<String> quchengData =new ArrayList<String>();
	ArrayList<String> fanchengData =new ArrayList<String>();
	public  WebDriver driver;
	
	public  void openWeb(String url) {
		System.setProperty("webdriver.chrome.driver", ".//chromedriver.exe");
		driver =  new ChromeDriver();
	    driver.manage().window().maximize();  //最大化窗口  
		//设置隐性等待时间  
	    driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS); 
	    driver.get(url);	
	}
	public void closeBrowser() {
		driver.quit();	
	}
	//显式等待
	public void waitUntilElementIsVisible(String locator, int timeout) {
		WebDriverWait wait = new WebDriverWait(driver, timeout);
		wait.until(ExpectedConditions.visibilityOf(driver.findElement(By.xpath(locator)))).isDisplayed();
	}
	//计算出当前日期+x天后的日期，返回一个计算后的年月日格式的日期
	public String getDate(int days) {
		Calendar calendar = Calendar.getInstance(); 
		//获取当前日期+x天后的日期 
	    calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + days); 
	    Date xxday = calendar.getTime();  //获取加x天后的“时间”赋值给Date格式对象xxday
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd"); //设置显示格式
	    String newDate = format.format(xxday);//按照格式转换成String，即年月日的格式
		return newDate;
	}
	//日历插件操作
	public void operator_Calendar(String newDate) {
		//获取网页上插件当前左右两个月的月份,为了确定输入的日期点击的位置在左还是右
		String leftMM =driver.findElement(By.xpath("//ul[@class='widget-calendar-hd']/li[1]//h3")).getText();//提取左边为："2022年 2月"
		String rightMM=driver.findElement(By.xpath("//ul[@class='widget-calendar-hd']/li[2]//h3")).getText();//提取右边为："2022年 3月"
		//System.out.println("左边："+leftMM+"--右边："+rightMM);
		//提取出我输入的月份，
		int start_M=Integer.valueOf(newDate.substring(5, 7));//2022-03-06,取03月份，string类型数字转化成int为3
		Pattern p = Pattern.compile("[^0-9]");  //正则表达式，提取网页月份的纯数字
		Matcher m = p.matcher(leftMM.substring(5,7)); // 2，有空格需要转化成int型纯数字
		int left_M=Integer.valueOf(m.replaceAll("").trim()) ;//2
		Matcher m2=p.matcher(rightMM.substring(5,7));//3
		int right_m=Integer.valueOf(m2.replaceAll("").trim());//3
		//System.out.println(left_M+"--"+right_m+"--"+start_M);
		//并比较月份		
		int index=0;
		if (left_M==start_M) {
			//System.out.println("从左边月份选日期");
			index=1;//左边的xpath表达式会定位到第一个div即div[1]；
					
		} else if(right_m==start_M){
			//System.out.println("从右边月份选日期");
			index=2;
		}else {
			System.out.println("月份不匹配需要切换月份后选日期");
		}
		//确定月份后选日期
		int select_day=Integer.valueOf(newDate.substring(8,10));//取出要输入的日期即日 06日，转化成int变成6
		//System.out.println("日期："+select_day);
		String day_xpath="//ul[@class='widget-calendar-hd']/following-sibling::div["+index
				+"]/ul[2]//strong[text()='"+select_day+"']/..";
		driver.findElement(By.xpath(day_xpath)).click();
	}
	
	//比较时长，保留和最小值相等的
	public boolean compareTime(String haoshi,String haoshi_mix) {
		boolean timetag=true;//haoshi ==haoshi_mix  返回最小值得那几条
		Pattern p = Pattern.compile("[^0-9]");  	
		int hour1=Integer.valueOf(p.matcher(haoshi.substring(0, 1)).replaceAll("").trim());//正则提取每一行票的小时
		int min1=Integer.valueOf(p.matcher(haoshi.substring(2, 4)).replaceAll("").trim());//正则提取每一行票的分钟
		//System.out.println(hour1+"--"+min1);
		int hour2=Integer.valueOf(p.matcher(haoshi_mix.substring(0,1)).replaceAll("").trim());//正则提取最小值的小时
		int min2=Integer.valueOf(p.matcher(haoshi_mix.substring(2, 4)).replaceAll("").trim());//正则提取最小值的分钟
		if (hour1>hour2) {//如果每一行的耗时小时数大于最小小时
			timetag=false;//haoshi>haoshi_mix--不打印
		} else if (hour1==hour2) {
			if (min1>min2) {
				timetag=false;//haoshi>haoshi_mix--不打印
			} else if(min1==min2){
				timetag=true;//haoshi ==haoshi_mix--打印
			}else {
				timetag=false;//haoshi<haos_mix--按短--长排序，不可能比最小值小--不存在不打印
			}
		}else {
			timetag=false;//haoshi<haos_mix--按短--长排序，不可能比最小值小---不存在不打印
		}
		return timetag;
	}
	
	public void buy() throws InterruptedException {
	
		String handle1=driver.getWindowHandle();
		//搜索“火车” id="_allSearchKeyword" id="search_button_global"
		driver.findElement(By.id("_allSearchKeyword")).clear();
		driver.findElement(By.id("_allSearchKeyword")).sendKeys("火车");
		Thread.sleep(500);
		driver.findElement(By.id("search_button_global")).click();//点击搜索按钮
		Thread.sleep(500);
		Set<String> allHandles=driver.getWindowHandles();//获取所有句柄
		Iterator<String> it = allHandles.iterator();//用迭代器作便利
		while (it.hasNext()) {
			String handle=it.next();
			if (!handle.equals(handle1)) {//下一个句柄不等于首页句柄就切换到新页面
				driver.switchTo().window(handle);
				System.out.println("切换页面到火车票页！！！");
				break;
			}
		}
		//断言页面出现“国内火车票”链接，判断已切换只火车票购买页面
		String alinktext=driver.findElement(By.id("c_ph_train_t")).getText();
		Assert.assertEquals(alinktext, "国内火车票");
		//点击“往返”
		waitUntilElementIsVisible("//button[text()='往返']/preceding-sibling::i", 10);//显示等待
		driver.findElement(By.xpath("//button[text()='往返']/preceding-sibling::i")).click();
		Thread.sleep(500);
		driver.findElement(By.id("label-arriveStation")).click();//点击城市输入框；弹窗城市列表
		Thread.sleep(500);
		driver.findElement(By.id("label-arriveStation")).clear();
		driver.findElement(By.id("label-arriveStation")).sendKeys("南京");
		Thread.sleep(500);
		Actions action=new Actions(driver);
		action.sendKeys(Keys.TAB).build().perform();//键盘tab收起城市弹框
		Thread.sleep(500);
		//driver.findElement(By.id("label-departDate")).sendKeys(start_date);//失败，日期不可直接输入
//		String jshell="document.getElementById(\"label-departDate\").value=\""+start_date +"\"";
//		JavascriptExecutor js=(JavascriptExecutor) driver;
//		js.executeScript(jshell);//js没有起作用
		String start_date=getDate(7);
		String return_date=getDate(14);
		System.out.println("出发："+start_date+"--返程："+return_date);
		//点击出发日期，弹出日期插件
		driver.findElement(By.xpath("//*[@id='label-departDate']/following-sibling::strong[1]")).click();
		//点击选择出发日期
		operator_Calendar(start_date);
		Thread.sleep(1000);
		//点击返程日期，弹出日期插件
		driver.findElement(By.xpath("//*[@id='label-returnDate']/following-sibling::strong[1]")).click();
		//点击选择返回日期
		operator_Calendar(return_date);
		Thread.sleep(500);
		driver.findElement(By.xpath("//button[@class='btn-blue btn-search']")).click();//点击搜索按钮
		Thread.sleep(5000);//等待搜索结果加载完成（不清楚会查出多少票用时多少，固用sleep时间稍长一点）
		Actions action2=new Actions(driver);
		for (int i = 0; i < 10; i++) {
			action2.sendKeys(Keys.DOWN).build().perform();//十次down键代替滚轮操作，页面上滑。
		}
		Thread.sleep(500);
		//查找去程票
		System.out.println("查找去程票");
		//点击展开全部，通过去程，的父级的叫div的弟弟，下面的第三个子节点div，下的第一个子节点
		driver.findElement(By.xpath("//div[1]/h3[text()='去程']/../following-sibling::div/div[3]/div")).click();
		Thread.sleep(500);
		//勾选二等座
		driver.findElement(By.xpath("//div[1]/h3[text()='去程']/../following-sibling::div[1]//strong[text()='二等座']/../i")).click();
		Thread.sleep(500);
		
		//点击运行时长--排序：短--长  取耗时最短的
		driver.findElement(By.xpath("//div[1]/h3[text()='去程']/../following-sibling::div/ul[2]//div[text()='运行时长']/..")).click();
		driver.findElement(By.xpath("//div[@class='widget-box widget-cert']/ul/li[1]")).click();
		Thread.sleep(500);
		//找出票信息的所有行，存入list
		List<WebElement> elementlist=driver.findElements(By.xpath("//div[1]/h3[text()='去程']/../following-sibling::section/div[@class='card-white list-item']"));
		//第一行票信息耗时时间最短，就是最小值
		String haoshi_mix=elementlist.get(0).findElement(By.xpath("//div/div[@class='mid']/div[@class='haoshi']")).getText();
		//打印出0时59分
		System.out.println("去程最短时长："+haoshi_mix);	
		//二等座(抢);暂无余票;二等座1张 
		//二等座有票
		for (int i = 1; i <= elementlist.size(); i++) {
			action2.sendKeys(Keys.DOWN).build().perform();
			action2.sendKeys(Keys.DOWN).build().perform();
			//	每一行票信息的行号
			String element_i_xpath="//div[1]/h3[text()='去程']/../following-sibling::section/div[@class='card-white list-item']["+i+"]";
			//每一行票信息的具体信息，耗时，车次，发车时间，价格，状态
			String haoshi=driver.findElement(By.xpath(element_i_xpath+"//div/div[@class='mid']/div[@class='haoshi']")).getText();
			String checi=driver.findElement(By.xpath(element_i_xpath+"//div/div[@class='mid']/div[@class='checi']")).getText();
			String fache_time=driver.findElement(By.xpath(element_i_xpath+"//div/div[@class='from']/div[@class='time']")).getText();
			String price=driver.findElement(By.xpath(element_i_xpath+"//div/div[@class='rbox']/div[@class='price']")).getText();
			String ticket_status=driver.findElement(By.xpath(element_i_xpath+"//ul/li[1]")).getText();
			//如果耗时等于最短，二等座有票，就存入list，
			if (compareTime(haoshi, haoshi_mix)) {
				if (ticket_status.contains("二等座有票")) {
					//System.out.println("车次："+checi+"--发车时间："+fache_time+"--耗时："+haoshi+"--价格："+price+"--是否有票："+ticket_status);
					//把票务信息存入list，add方法存入
					quchengData.add("车次："+checi+"--发车时间："+fache_time+"--耗时："+haoshi+"--价格："+price+"--是否有票："+ticket_status);
				}
			} else {
				System.out.println("去程：耗时最短的车次查找完成");
				break;
			}
		}
		//查找返程票
		System.out.println("查找返程票");
		for (int i = 0; i < 5; i++) {//向上点5下，找到点开全部按钮
			action2.sendKeys(Keys.UP).build().perform();
		}
		Thread.sleep(200);
		//点击展开全部
		driver.findElement(By.xpath("//div[1]/h3[text()='返程']/../following-sibling::div/div[3]/div")).click();
		Thread.sleep(500);
		//勾选二等座
		driver.findElement(By.xpath("//div[1]/h3[text()='返程']/../following-sibling::div[1]//strong[text()='二等座']/../i")).click();
		Thread.sleep(500);
				
		//点击运行时长--排序短--长
		driver.findElement(By.xpath("//div[1]/h3[text()='返程']/../following-sibling::div/ul[2]//div[text()='运行时长']/..")).click();
		driver.findElement(By.xpath("//div[@style='position: absolute; top: 0px; left: 0px; width: 100%;'][2]//ul/li[1]")).click();
		Thread.sleep(500);
		
		List<WebElement> elementlist2=driver.findElements(By.xpath("//div[1]/h3[text()='返程']/../following-sibling::section/div[@class='card-white list-item']"));
		String haoshi_mix2=driver.findElement(By.xpath("//div[1]/h3[text()='返程']/../following-sibling::section/div[@class='card-white list-item'][1]//div/div[@class='mid']/div[@class='haoshi']")).getText();
		System.out.println("返程最短时长："+haoshi_mix2);	
		//二等座(抢);暂无余票;二等座1张 二等座有票
		for (int i = 1; i <= elementlist2.size(); i++) {
			action2.sendKeys(Keys.DOWN).build().perform();
			action2.sendKeys(Keys.DOWN).build().perform();
			String element_i_xpath="//div[1]/h3[text()='返程']/../following-sibling::section/div[@class='card-white list-item']["+i+"]";
			String haoshi=driver.findElement(By.xpath(element_i_xpath+"//div/div[@class='mid']/div[@class='haoshi']")).getText();
			String checi=driver.findElement(By.xpath(element_i_xpath+"//div/div[@class='mid']/div[@class='checi']")).getText();
			String fache_time=driver.findElement(By.xpath(element_i_xpath+"//div/div[@class='from']/div[@class='time']")).getText();
			String price=driver.findElement(By.xpath(element_i_xpath+"//div/div[@class='rbox']/div[@class='price']")).getText();
			String ticket_status=driver.findElement(By.xpath(element_i_xpath+"//ul/li[1]")).getText();
			if (compareTime(haoshi, haoshi_mix2)) {
				if (ticket_status.contains("二等座有票")) {
					//System.out.println("车次："+checi+"--发车时间："+fache_time+"--耗时："+haoshi+"--价格："+price+"--是否有票："+ticket_status);
					fanchengData.add("车次："+checi+"--发车时间："+fache_time+"--耗时："+haoshi+"--价格："+price+"--是否有票："+ticket_status);
				}
			} else {
				System.out.println("返程：耗时最短的车次查找完	成");
				break;
			}		
		}
		
	}

	public static void main(String[] args) throws InterruptedException {
		OpenXieCheng open=new OpenXieCheng();
		//open.openWeb(open.xiechengUrl);
		//open.buy();
		
	}

}
