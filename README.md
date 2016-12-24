# CallLogger
Telefon görüşmelerini kaydetmek için geliştirilmiş bir uygulama.

Normal kayıt programlarından farkı; **kayıt bilgilerini istenen bir web sunucuya gönderir.**

Web sunucuya aktarma işlemi Ayarlar sayfasından aktif/pasif yapılabilir. (Bu ayar pasif yapılsa da program kayıt bilgilerini kendi veritabanında tutar).
Sunucuya verileri göndermek için; sunucu adresi, kullanıcı adı ve şifre girilmesi yeterlidir.

İstenirse sadece WiFi ile internet erişimi varken veri gönderimi sağlanır.

Veriler, sunucuya **JSON** formatında **POST** edilir ve bu işlem **HTTP Basic Authentication** kullanılarak yapılır.

### Ekran Görüntüleri:

![Ekran Görüntüsü 1](https://github.com/SimaWB/CallLogger/blob/master/Screenshots/screenshot1.jpg)

![Ekran Görüntüsü 2](https://github.com/SimaWB/CallLogger/blob/master/Screenshots/screenshot2.jpg)

