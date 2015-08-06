package eu.eexcess.dataformats.userprofile;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

import org.junit.Test;

public class SecureUserProfileEvaluationTest {
    @Test
    public void xmlSerialisationTest() {
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(SecureUserProfileEvaluation.class);
        } catch (JAXBException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            assert (false);
        }
        Marshaller m = null;
        try {
            m = context.createMarshaller();
        } catch (JAXBException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
            assert (false);
        }
        try {
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        } catch (PropertyException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            assert (false);
        }

        SecureUserProfileEvaluation evalProfile = new SecureUserProfileEvaluation();
        evalProfile.contextKeywords.add(new ContextKeyword("Test"));
        try {
            m.marshal(evalProfile, System.out);
        } catch (JAXBException e) {
            // TODO Auto-generated catch block

            e.printStackTrace();
            assert (false);
        }
        assert (true);
    }
}
