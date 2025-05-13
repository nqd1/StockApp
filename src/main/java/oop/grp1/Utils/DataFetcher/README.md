



# DataFetcher – Lấy dữ liệu tài chính từ AlphaVantage & Polygon.io

## 📦 Giới thiệu

Thư mục `oop.grp1.Utils.DataFetcher` chứa hai class Java đơn giản dùng để truy vấn dữ liệu thị trường tài chính theo thời gian thực từ hai dịch vụ phổ biến:

* `AlphaVantage`: Lấy dữ liệu giá cổ phiếu theo từng 5 phút.
* `PolygonIO`: Lấy dữ liệu giá đóng cửa của ngày gần nhất.

---

## 🧩 Cấu trúc class

### 1. `AlphaVantage`

* **Mục đích**: Truy vấn dữ liệu chuỗi thời gian nội ngày (Intraday) cho một mã cổ phiếu từ API của Alpha Vantage.
* **API endpoint**:

  ```
  https://www.alphavantage.co/query
  ```
* **Tham số mặc định**:

  * function: `TIME_SERIES_INTRADAY`
  * interval: `5min`
  * outputsize: `compact`
* **API Key (mặc định)**: lấy từ .env `ALPHA_VANTAGE_API_KEY`
* **Phương thức chính**:

  ```java
  public String getData(String symbol)
  ```
* **Định dạng trả về**: Kết quả trả về là một chuỗi JSON chứa thông tin về giá cổ phiếu trong khoảng thời gian đã chỉ định.
* **Ví dụ**:

  ```json
  {
      "Meta Data": {
          "1. Information": "Intraday (5min) open, high, low prices and volume",
          "2. Symbol": "AAPL",
          "3. Last Refreshed": "2000-01-01 16:00:00",
          "4. Interval": "5min",
          "5. Output Size": "Compact",
          "6. Time Zone": "US/Eastern"
      },
      "Time Series (5min)": {
          "2023-10-20 16:00:00": {
              "1. open": "174.0000",
              "2. high": "175.0000",
              "3. low": "173.0000",
              "4. close": "174.5000",
              "5. volume": "1000000"
          },
          ...
      }
  }
  ```
### 2. `PolygonIO`

* **Mục đích**: Lấy dữ liệu phiên giao dịch trước đó cho một mã cổ phiếu từ Polygon.io.
* **API endpoint**:

  ```
  https://api.polygon.io/v2/aggs/ticker/{symbol}/prev
  ```
* **Tham số mặc định**:

  * adjusted: `true`
* **API Key (mặc định)**: lấy từ .env `POLYGON_API_KEY`
* **Phương thức chính**:

  ```java
  public String getData(String symbol)
  ```
* **Định dạng trả về**: Kết quả trả về là một chuỗi JSON chứa thông tin về giá cổ phiếu trong phiên giao dịch trước đó.
* **Ví dụ**:

  ```json
  {
      "ticker": "AAPL",
      "queryCount": 1,
      "resultsCount": 1,
      "adjusted": true,
      "queryCountAdjusted": 1,
      "resultsCountAdjusted": 1,
      "status": "OK",
      "results": [
          {
              "v": 1000000,
              "vw": 174.5000,
              "o": 174.0000,
              "c": 175.0000,
              "h": 175.0000,
              "l": 173.0000,
              "t": 1697817600000,
              ...
          }
      ]
  }
  ```

---

## 📌 Ghi chú

* Cần kiểm tra quota của từng API key do có thể bị giới hạn số lần gọi miễn phí.
* Class hiện trả về kết quả JSON dưới dạng `String`. Class JsonBeautifier được sử dụng để định dạng lại JSON cho dễ đọc hơn.


