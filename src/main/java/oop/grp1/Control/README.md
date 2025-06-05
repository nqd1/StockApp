# Thư mục Control

Thư mục này chứa các thành phần điều khiển chính cho ứng dụng chứng khoán. Các chức năng chính bao gồm lấy dữ liệu từ API và quản lý cơ sở dữ liệu.

## Cấu trúc thư mục

### DataFetcher
Chứa các lớp chịu trách nhiệm kết nối và lấy dữ liệu từ API Alpha Vantage:
- `DFAbstract.java`: Lớp trừu tượng cơ sở cho các DataFetcher
- `StockDF.java`: Lấy dữ liệu chứng khoán (cổ phiếu) theo thời gian thực
- `NewsDF.java`: Lấy dữ liệu tin tức và phân tích tình cảm liên quan đến chứng khoán 

### DBManager
Chứa các lớp quản lý việc lưu trữ dữ liệu vào cơ sở dữ liệu SQLite:
- `DBManager.java`: Lớp trừu tượng cơ sở cho các Manager
- `StockManager.java`: Xử lý lưu trữ dữ liệu chứng khoán
- `NewsManager.java`: Xử lý lưu trữ dữ liệu tin tức

### Tiện ích
- `JsonBeautifier.java`: Công cụ định dạng JSON để dễ đọc

## Luồng dữ liệu
1. Các lớp DataFetcher gọi API Alpha Vantage để lấy dữ liệu JSON
2. Dữ liệu được xử lý và chuyển đến các DBManager tương ứng
3. DBManager phân tích dữ liệu JSON và lưu trữ vào cơ sở dữ liệu SQLite

## Cách sử dụng

### Lấy và lưu dữ liệu chứng khoán
```java
StockManager stockManager = new StockManager();
stockManager.fetchAndStore("AAPL"); // Lấy và lưu dữ liệu cổ phiếu Apple
```

### Lấy và lưu dữ liệu tin tức
```java
NewsManager newsManager = new NewsManager();
newsManager.fetchAndStore("TSLA"); // Lấy và lưu tin tức về Tesla
```

## Lưu ý
- Đảm bảo các khóa API Alpha Vantage được cấu hình đúng trong file `.env`
- Cơ sở dữ liệu SQLite (stockAV.db) sẽ được tạo tự động nếu chưa tồn tại 