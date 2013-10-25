

/* First created by JCasGen Thu Oct 24 23:38:50 EDT 2013 */
package edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems;

import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP_Type;

import org.apache.uima.jcas.cas.FSList;
import org.apache.uima.jcas.tcas.Annotation;


/** Stores the document and its score
 * Updated by JCasGen Fri Oct 25 09:36:40 EDT 2013
 * XML source: C:/Users/gandhe/Dropbox/Semester 3/Software_Engineering/Assign4/hw4-ankurgan/src/main/resources/descriptors/typesystems/VectorSpaceTypes.xml
 * @generated */
public class DocumentScore extends Annotation {
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(DocumentScore.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated  */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
  /** Never called.  Disable default constructor
   * @generated */
  protected DocumentScore() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated */
  public DocumentScore(int addr, TOP_Type type) {
    super(addr, type);
    readObject();
  }
  
  /** @generated */
  public DocumentScore(JCas jcas) {
    super(jcas);
    readObject();   
  } 

  /** @generated */  
  public DocumentScore(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
    readObject();
  }   

  /** <!-- begin-user-doc -->
    * Write your own initialization here
    * <!-- end-user-doc -->
  @generated modifiable */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: score

  /** getter for score - gets the score of the document
   * @generated */
  public float getScore() {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    return jcasType.ll_cas.ll_getFloatValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_score);}
    
  /** setter for score - sets the score of the document 
   * @generated */
  public void setScore(float v) {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_score == null)
      jcasType.jcas.throwFeatMissing("score", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    jcasType.ll_cas.ll_setFloatValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_score, v);}    
   
    
  //*--------------*
  //* Feature: text

  /** getter for text - gets Stores the text of the document
   * @generated */
  public String getText() {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    return jcasType.ll_cas.ll_getStringValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_text);}
    
  /** setter for text - sets Stores the text of the document 
   * @generated */
  public void setText(String v) {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_text == null)
      jcasType.jcas.throwFeatMissing("text", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    jcasType.ll_cas.ll_setStringValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_text, v);}    
   
    
  //*--------------*
  //* Feature: queryID

  /** getter for queryID - gets Stores the query ID of the document
   * @generated */
  public int getQueryID() {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_queryID == null)
      jcasType.jcas.throwFeatMissing("queryID", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    return jcasType.ll_cas.ll_getIntValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_queryID);}
    
  /** setter for queryID - sets Stores the query ID of the document 
   * @generated */
  public void setQueryID(int v) {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_queryID == null)
      jcasType.jcas.throwFeatMissing("queryID", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    jcasType.ll_cas.ll_setIntValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_queryID, v);}    
   
    
  //*--------------*
  //* Feature: relevanceValue

  /** getter for relevanceValue - gets Stores the relevance of the document
   * @generated */
  public int getRelevanceValue() {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_relevanceValue == null)
      jcasType.jcas.throwFeatMissing("relevanceValue", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    return jcasType.ll_cas.ll_getIntValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_relevanceValue);}
    
  /** setter for relevanceValue - sets Stores the relevance of the document 
   * @generated */
  public void setRelevanceValue(int v) {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_relevanceValue == null)
      jcasType.jcas.throwFeatMissing("relevanceValue", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    jcasType.ll_cas.ll_setIntValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_relevanceValue, v);}    
   
    
  //*--------------*
  //* Feature: tokenList

  /** getter for tokenList - gets 
   * @generated */
  public FSList getTokenList() {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_tokenList == null)
      jcasType.jcas.throwFeatMissing("tokenList", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    return (FSList)(jcasType.ll_cas.ll_getFSForRef(jcasType.ll_cas.ll_getRefValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_tokenList)));}
    
  /** setter for tokenList - sets  
   * @generated */
  public void setTokenList(FSList v) {
    if (DocumentScore_Type.featOkTst && ((DocumentScore_Type)jcasType).casFeat_tokenList == null)
      jcasType.jcas.throwFeatMissing("tokenList", "edu.cmu.lti.f13.hw4.hw4_ankurgan.typesystems.DocumentScore");
    jcasType.ll_cas.ll_setRefValue(addr, ((DocumentScore_Type)jcasType).casFeatCode_tokenList, jcasType.ll_cas.ll_getFSRef(v));}    
  }

    