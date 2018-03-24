package com.mykaarma.test.publisher.runner;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
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
	@Autowired
	private ResourceLoader resourceLoader;
	
	String xml = null, toConcat = "</CheckSum></RepairOrderWrap>";
	
	@PostConstruct
	void loadXML() throws IOException{
		Resource resource = resourceLoader.getResource("classpath:286952.xml");
		try {
			xml = new String(Files.readAllBytes(resource.getFile().toPath()));
			LOGGER.info("XML Loaded into Memory");
		} catch (IOException e) {
			throw e;
		}
	}
	
	
	@RequestMapping(value="/saveKaarmaDealerOrderXml", method=RequestMethod.GET)
	public String saveKaarmaDealerOrderXML()  throws Exception
	{
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
		}
		return null;
	}
}
