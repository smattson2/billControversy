package ArticleFetcher;

import java.util.LinkedHashMap;

public class RelatedBillMerger {
	
	//DEPRECATED
	
	/*
	
	//private boolean deleteThisBill = false;
	private final Bill bill;
	private final LinkedHashMap<String, Bill> billMap;
	//private boolean mergeCompleted = false;
	
	public RelatedBillMerger(Bill bill, LinkedHashMap<String, Bill> billMap){
		this.bill = bill;
		this.billMap = billMap;
	}

	//DEPRECATED
	/*
	public boolean billMustBeDeleted(){
		if(!mergeCompleted){
			throw new IllegalStateException("Merge related bills before checking if this one must be deleted.");
		}
		return deleteThisBill;
	}
	*/
	
	public void mergeRelatedBills(){
		for(int i = 0; i < bill.getRelated_bills().length; i++){
			Bill.RelatedBill otherBill = bill.getRelated_bills()[i];
			if(otherBill.getType() == "bill"){
				switch(otherBill.getReason()){
			
			//Only merging clear-cut cases: supersedes and included in	
			/*		case "identical":
						unknown(otherBill.getBill_id());
						break;
					case "related":
						unknown(otherBill.getBill_id());
						break;
					case "unknown":
						unknown(otherBill.getBill_id());
						break;
			*/
					case "supersedes":
						System.out.println("Supersedes");
						supersedes(otherBill.getBill_id());
						break;
					case "included-in":
						System.out.println("Included in");
						includedIn(otherBill.getBill_id());
						break;
					default:
						break;
				}	
			}
		}
//		mergeCompleted = true;
	}
	
	//Doesn't change the top-level titles, only the array of all.
	private void mergeTitles(Bill primary, Bill secondary){
		primary.addMoreTitles(secondary.getTitles());
	}
	
	/*
	 * Must determine which bill should be considered primary.
	 * Checks criteria in this order:
	 * 1. If one was enacted and the other wasn't.
	 * 2. If one was passed and the other wasn't.
	 * 3. Defaults to other one being primary (it hasn't been tested yet!)
	 */
	
	//DEPRECATED
	/*
	private void unknown(String bill_id) {
		Bill otherBill = billMap.get(bill_id);
		//Not booleans because their results are going to Stata in the main code. Sorry.
		if((bill.enacted() == 1) && (otherBill.enacted() != 1)){
			includedIn(bill_id);
			return;
		}
		if((otherBill.enacted() == 1) && (bill.enacted() != 1)){
			supersedes(bill_id);
			return;
		}
		if((bill.passed() == 1) && (otherBill.passed() != 1)){
			includedIn(bill_id);
			return;
		}
		if((otherBill.passed() == 1) && (bill.passed() != 1)){
			supersedes(bill_id);
			return;
		}
		supersedes(bill_id);
	}

	*/
	
	//Considers the other bill to be the primary bill.
	private void supersedes(String bill_id) {
		Bill toKeep = billMap.get(bill_id);
		mergeTitles(toKeep, bill);
		//Shouldn't need to merge subjects if one bill is included in the other.
		//Should be done already, by the Library of Congress.
		bill.setShouldRemove(true);
	}

	//Considers this bill to be the primary bill.
	private void includedIn(String bill_id) {
		Bill otherBill = billMap.get(bill_id);
		mergeTitles(bill, otherBill);
		//Shouldn't need to merge subjects if one bill is included in the other.
		//Should be done already, by the Library of Congress.
		otherBill.setShouldRemove(true);
	}
	
	*/
	
}


