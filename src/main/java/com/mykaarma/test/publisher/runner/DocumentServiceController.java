package com.mykaarma.test.publisher.runner;

import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import com.mykaarma.test.publisher.model.KaarmaEntityXML;


@Controller
@RequestMapping("/docservice")
public class DocumentServiceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentServiceController.class);
	@Autowired
	private Sender documentService;
	
	String xml = "<RepairOrderWrap><Dealer><DealerID/><DealerName/><DealerDMS><DealerDMSID/><DMSName/><UserName/><Password/><HostUrl/></DealerDMS></Dealer><Source>AdpPdfManager</Source><Invoice/><LaborTypes><LaborType>CP</LaborType></LaborTypes><RepairOrder><ID/><OrderNumber>286952</OrderNumber><OrderType>RO</OrderType><Amount/><OrderDate>2017-08-10 14:26:02</OrderDate><AssociateDMSID>995952</AssociateDMSID><DealerAssociateID/><Description/><OrderStatus>READY TO POST</OrderStatus><NumberOfInvoices/><ReadyROData/><IsPaid/><PaidAmount/><IsPaidInKaarma/><IsPaymentRequestSent/><Tag>TO0966</Tag><MileageText/><InvoiceUrl/><PdfRequestAmtRef/><LabourOpTypes>INFLATE,MISC,MPIS</LabourOpTypes><DepartmentType>S</DepartmentType><CustPayTotal>260.58</CustPayTotal></RepairOrder><CustWrap><CAttrs><IsBusiness/><AssignedSA/><Comments/></CAttrs><IdentityAttrs><Attr value=\"SSN\"/><Attr value=\"DriverLicense\"/><Attr value=\"BirthDate\"/><Attr value=\"Gender\"/><Attr value=\"Language\"/></IdentityAttrs><CustPref><Pref value=\"\"/><Pref value=\"text\">Y</Pref><Pref value=\"email\">Y</Pref><Pref value=\"call\">Y</Pref><Pref value=\"phone\"/><Pref value=\"postal\"/></CustPref><PreferredComm><Preferred><Mode/><Value/><Time/></Preferred><Preferred><Mode/><Value/><Time/></Preferred></PreferredComm><Customer><ID/><Version/><CustomerKey/><TypeCode/><CustomerType/><DealerID/><FName>Samir</FName><MName/><LName>Khoury</LName><ImageUrl/><Indexed/><Valid/><OptedOut/><UpdatedBy/><CreatedBy/><Communications><Communication><ID/><Version/><Type>E</Type><Value>KHOURY418@HOTMAIL.COM</Value><CreatedBy/><Preferred/><Valid>1</Valid><Label>work</Label><Desc/><UpdatedBy/><CommPrefs/></Communication><Communication><ID/><Version/><Type>P</Type><Value>(626)863-5805</Value><CreatedBy/><Preferred/><Valid>1</Valid><Label>Cell</Label><Desc/><UpdatedBy/><CommPrefs/></Communication><Communication><ID/><Version/><Type>P</Type><Value>(626)804-3530</Value><CreatedBy/><Preferred/><Valid>1</Valid><Label>Home</Label><Desc/><UpdatedBy/><CommPrefs/></Communication><Communication><ID/><Version/><Type>P</Type><Value>(626)863-5805</Value><CreatedBy/><Preferred/><Valid>1</Valid><Label>Work</Label><Desc/><UpdatedBy/><CommPrefs/></Communication></Communications><Addresses/></Customer><LastModified/><StatusCode/><StatusMessage/></CustWrap>", toConcat = "</CheckSum></RepairOrderWrap>";
	
	@PostConstruct
	void Log(){
		LOGGER.info("App Loaded!");
	}
	
	@RequestMapping(value="/saveKaarmaDealerOrderXml", method=RequestMethod.GET)
	public String saveKaarmaDealerOrderXML()  throws Exception
	{
		String ros = "";
		for(int i=0; i<100; i++) {
			String msgId = UUID.randomUUID().toString();
			String finXml = xml + "<CheckSum>"+msgId+toConcat;
			KaarmaEntityXML kaarmaEntityXML = new KaarmaEntityXML();
			kaarmaEntityXML.setDealerID(67l);
			kaarmaEntityXML.setEntityKey("TESTING-"+i);
			kaarmaEntityXML.setXml(finXml);
			kaarmaEntityXML.setMessageID(msgId);
			kaarmaEntityXML.setOriginDate(new Date());
			LOGGER.info("Pushing "+msgId+" into Mongo");
			documentService.saveKaarmaROXMLAndPublishToDocQueue(kaarmaEntityXML);
			ros+="TESTING-"+i;
		}
		return ros;
	}
}
