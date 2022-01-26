package com.example.ecoin;
import java.util.ArrayList;
import java.util.Date;

public class Block {

	public String hash;
	public String previousHash;
	public String merkleRoot;
	public ArrayList<Transaction> transactions = new ArrayList<Transaction>();
	public long timeStamp; //as number of milliseconds since 1/1/1970.
	public int nonce;

	public Block(String previousHash ) {
		this.previousHash = previousHash;
		this.timeStamp = new Date().getTime();
		this.hash = calculateHash();
	}

	// Calculate a hash for a block
	public String calculateHash() {
		String hash = StringUtil.applySha256(previousHash + Long.toString(timeStamp) + Integer.toString(nonce) + merkleRoot);
		return hash;
	}

	// Increases nonce value until hash target is reached.
	public void mineBlock(int difficulty) {
		merkleRoot = StringUtil.getMerkleRoot(transactions);
		String target = StringUtil.getDificultyString(difficulty);
		while(!hash.substring( 0, difficulty).equals(target)) {
			nonce ++;
			hash = calculateHash();
		}
		System.out.println("Block " + hash + " mined." );
	}

	// Add transactions to the Block
	public boolean addTransaction(Transaction transaction) {
		// Check if transaction is valid with special exception for genesis Block.
		if(transaction == null) {
			return false;
		}
		if((!"0".equals(previousHash))) {
			if((transaction.processTransaction() != true)) {
				System.out.println("Transaction failed.");
				return false;
			}
		}

		transactions.add(transaction);
		System.out.println("Transaction added to Block");
		return true;
	}

}
