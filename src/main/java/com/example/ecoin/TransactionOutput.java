package com.example.ecoin;
import java.security.PublicKey;

public class TransactionOutput {
	public String id;
	public PublicKey reciepient; //odbiorca (nowy właściciel)
	public float value; //kwota
	public String parentTransactionId; //identyfikator tranzakcji do którego należy ten output
	
	//Konstruktor
	public TransactionOutput(PublicKey reciepient, float value, String parentTransactionId) {
		this.reciepient = reciepient;
		this.value = value;
		this.parentTransactionId = parentTransactionId;
		this.id = StringUtil.applySha256(StringUtil.getStringFromKey(reciepient)+Float.toString(value)+parentTransactionId);
	}
	
	//sprawdż czy coin należy do ciebie
	public boolean isMine(PublicKey publicKey) {
		return (publicKey == reciepient);
	}
	
}
