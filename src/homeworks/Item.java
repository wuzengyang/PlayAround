package homeworks;

import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

public class Item {
	private String status;

	public String getFormatted_address() {
		return status;
	}	
	
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("status", status);

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}

	
	
	/**
	 * This is a builder pattern in Java.
	 */
	public Item(ItemBuilder builder) {
		this.status = builder.status;

	}

	public static class ItemBuilder {
		private String status;

		public ItemBuilder setAddress(String status) {
			this.status = status;
			return this;
		}


		public Item build() {
			return new Item(this);
		}
	}



}
