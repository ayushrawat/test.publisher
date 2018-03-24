package com.mykaarma.test.publisher.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mykaarma.test.publisher.model.KaarmaEntityXML;


@Controller
@RequestMapping("/docservice")
public class DocumentServiceController {

	@Autowired
	private Sender documentService;
	
	
	@RequestMapping(value="/saveKaarmaDealerOrderXml", method=RequestMethod.POST)
	public @ResponseBody KaarmaEntityXML saveKaarmaDealerOrderXML(@RequestBody KaarmaEntityXML kaarmaEntityXML)  throws Exception
	{
		return documentService.saveKaarmaROXMLAndPublishToDocQueue(kaarmaEntityXML);		
	}
}
