/**
 * 
 */
package com.mykaarma.test.publisher.runner;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mykaarma.test.publisher.config.MongoCollections;
import com.mykaarma.test.publisher.model.KaarmaEntityXML;




/**
 * @author root
 *
 */
@Component
public class Sender {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Sender.class);
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	private RabbitTemplate rabbitTemplate;

	private KaarmaEntityXML saveKaarmaEntityXMLToDocStore(KaarmaEntityXML kaarmaEntityXML, String collectionName){
		
		try {
			Query query = new Query(Criteria.where("entityKey").is(kaarmaEntityXML.getEntityKey()).and("dealerID").is(kaarmaEntityXML.getDealerID()));
			KaarmaEntityXML existingXML =(KaarmaEntityXML) mongoTemplate.findOne(query, KaarmaEntityXML.class,collectionName);
			if(existingXML != null)
			{
				if(kaarmaEntityXML.getOriginDate() != null && existingXML.getOriginDate() != null 
						&&kaarmaEntityXML.getOriginDate().compareTo(existingXML.getOriginDate())<0)
				{
					LOGGER.error("Ignoring MessageID "+ kaarmaEntityXML.getMessageID()+
							" since newer copy is available in kaarma doc store. ");
					return existingXML;
					
				}
				else
				{
					existingXML.setOriginDate(kaarmaEntityXML.getOriginDate());
					existingXML.setXml(kaarmaEntityXML.getXml());
					existingXML.setUpdatedDate(new Date());
					mongoTemplate.save(existingXML, collectionName);
					LOGGER.info("XML corresponding to MessageID "+kaarmaEntityXML.getMessageID()+" saved to Doc Store");
					return existingXML;
				}
				
			}
			else
			{
				kaarmaEntityXML.setUpdatedDate(new Date());
				mongoTemplate.save(kaarmaEntityXML, collectionName);
				LOGGER.info("XML corresponding to MessageID "+kaarmaEntityXML.getMessageID()+" saved to Doc Store");
				return kaarmaEntityXML;
			}
		} catch (Exception e) {
			LOGGER.error("FAILED for MessageID "+kaarmaEntityXML.getMessageID(), e);
			throw e;
		}
		
	}
	
	public KaarmaEntityXML saveKaarmaROXMLAndPublishToDocQueue(KaarmaEntityXML kaarmaEntityXML) throws Exception{
		KaarmaEntityXML obj = saveKaarmaEntityXMLToDocStore(kaarmaEntityXML,MongoCollections.KAARMADEALERORDER.name() ) ;
		rabbitTemplate.convertAndSend("kaarma.dms.entity.update.exchange", "kaarma.dms.entity.update.dealerOrder.key", getDocumentMQKey(kaarmaEntityXML));
		return obj;
	}
	

	
	private String getDocumentMQKey(KaarmaEntityXML kaarmaEntityXML) throws Exception{
		ObjectMapper mapper= new ObjectMapper();
		com.mykaarma.test.publisher.model.MQEntity mq=  new com.mykaarma.test.publisher.model.MQEntity();
		mq.setDealerID(kaarmaEntityXML.getDealerID());
		mq.setEntityID(kaarmaEntityXML.getEntityKey());
		mq.setMessageID(kaarmaEntityXML.getMessageID());
		return mapper.writeValueAsString(mq);
	}

}
