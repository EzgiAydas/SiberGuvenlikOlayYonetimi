🛡️ Siber Güvenlik Olay Yönetimi Sistemi (Security Incident Management)

Bu proje, Nesneye Yönelik Programlama (NYP) dersi proje ödevi kapsamında geliştirilmiş kapsamlı bir Siber Güvenlik Olay Yönetimi simülasyonudur. Sistem; ağ ve sistemlerde meydana gelen siber güvenlik olaylarının (Phishing, DDoS, Malware vb.) tespit edilmesi, sınıflandırılması, önem derecesine göre önceliklendirilmesi ve uzman analistlere atanarak müdahale süreçlerinin takip edilmesi amacıyla tasarlanmıştır.

📌 Ödev Kurallarına Uygunluk Özeti

Projemiz, proje yönergesinde belirtilen tüm isterleri eksiksiz bir şekilde karşılamaktadır:

Konsol Uygulaması ve Kalıcı Veri: Sistem tamamen konsol üzerinden interaktif bir menü ile çalışmakta olup, veriler FileIncidentRepository üzerinden dosyaya yazılarak (örn: incidents.ser) kalıcı olarak saklanmaktadır.

Sınıf Sayısı: Projede 8'den fazla anlamlı sınıf, arayüz (interface) ve enum (SecurityIncident, Analyst, IncidentManager, Strateji sınıfları vb.) kullanılmıştır.

Algoritma ve Birim Testi: Olayların SLA sürelerine ve analist iş yüküne göre dinamik olarak önceliklendirilmesini/atanmasını sağlayan algoritmalar geliştirilmiş ve projeye en az 8 adet birim testi (Unit Test) entegre edilmiştir.

UML: Projenin mimarisini anlatan detaylı UML sınıf diyagramı hazırlanmış ve depoya eklenmiştir.

🧬 Nesneye Yönelik Programlama (OOP) Kullanımı

Projenin temelinde NYP'nin 4 temel prensibi aktif olarak kullanılmıştır:

Kapsülleme (Encapsulation): Tüm sınıflardaki nitelikler (fields) private (-) veya protected (#) olarak tanımlanmış, dışarıdan kontrolsüz erişim engellenmiştir. Veri alışverişi kontrollü getter ve setter metotlarıyla sağlanmıştır.

Kalıtım (Inheritance): Ortak özellikleri barındıran SecurityIncident üst sınıfından, spesifik olay türlerini temsil eden alt sınıflar (PhishingIncident, DDoSIncident, MalwareIncident vb.) türetilmiştir.

Soyutlama (Abstraction): SecurityIncident soyut (abstract) bir sınıf olarak tasarlanmış olup, doğrudan nesne üretimi engellenmiştir. Ayrıca PriorityStrategy, AnalystAssigner ve IncidentRepository gibi arayüzler (interfaces) ile alt sistemlerin detayları gizlenmiştir.

Çok Biçimlilik (Polymorphism): SecurityIncident referansı kullanılarak farklı olay türleri (Phishing, DDoS) tek bir koleksiyonda yönetilebilmektedir. Farklı strateji sınıfları aynı metot isimleriyle (örn. calculate()) kendi iç mantıklarını çalıştırır.

🏛️ Tasarım Kalıpları (Design Patterns)

Projede esnekliği artırmak ve kod tekrarını önlemek için 3 farklı tasarım kalıbı uygulanmıştır:

Factory Pattern (Yaratımsal): IncidentFactory sınıfı, gelen talebe ve olay tipine göre (IncidentType) çalışma zamanında (runtime) doğru olay nesnesini (Phishing, DDoS vb.) üretmekle görevlidir.

Strategy Pattern (Davranışsal): Olayların öncelik skorlarının hesaplanmasında tek bir sabit formül yerine PriorityStrategy arayüzü kullanılmıştır. WeightedPriorityStrategy ve SlaAwarePriorityStrategy sınıfları sayesinde hesaplama algoritmaları dinamik olarak değiştirilebilir.

Observer Pattern (Davranışsal): IncidentObserver yapısı ile olaylarda durum (status) değişikliği olduğunda veya kritik bir olay eklendiğinde, sisteme kayıtlı bileşenlerin (örn. CriticalAlertObserver) otomatik olarak uyarılması sağlanmıştır.

📐 SOLID Prensipleri

Yazılımın sürdürülebilirliğini sağlamak için SOLID prensiplerine sıkı sıkıya uyulmuştur:

S - Single Responsibility Principle (Tek Sorumluluk): Sınıflar sadece kendi görevlerini yapar. IncidentManager iş mantığını yürütürken, ConsoleMenu arayüzü, FileIncidentRepository ise sadece veritabanı işlemlerini yönetir.

O - Open/Closed Principle (Açık/Kapalı): Sisteme yeni bir güvenlik olayı türü veya yeni bir öncelik stratejisi eklenmek istendiğinde mevcut kodlar değiştirilmez; sadece ilgili arayüzlerden (interface/abstract) yeni sınıflar türetilir.

L - Liskov Substitution Principle (Yerine Geçme): SecurityIncident beklenen her yere alt sınıfları (MalwareIncident vb.) gönderilebilir ve sistem hatasız çalışmaya devam eder.

D - Dependency Inversion Principle (Bağımlılığı Tersine Çevirme): Üst seviye yöneticiler (örn. IncidentManager), alt seviye dosya işlemlerine (FileIncidentRepository) doğrudan değil, IncidentRepository arayüzü üzerinden bağlanmıştır.

📊 UML Sınıf Diyagramı

Sistemin mimari yapısını ve sınıflar arası ilişkileri (kalıtım, gerçekleştirim, bağımlılık) gösteren UML sınıf diyagramı aşağıdadır:

(Diyagram görüntülenemiyorsa, depo içerisindeki UML_Sinif_Diyagrami.png dosyasına göz atabilirsiniz.)

🚀 Kurulum ve Çalıştırma

Projeyi bilgisayarınıza klonlayın:

git clone https://github.com/EzgiAydas/SiberGuvenlikOlayYonetimi.git


Favori Java IDE'niz (IntelliJ IDEA, Eclipse vb.) ile projeyi açın.

Bağımlılıkların ve SDK ayarlarının yapıldığından emin olun.

src/Main.java (veya ilgili başlangıç sınıfı) dosyasını çalıştırın.

Açılan interaktif konsol ekranındaki yönergeleri takip ederek sistemi kullanmaya başlayabilirsiniz.

Not: Grup üyelerinin öğrenci numarası, adı, soyadı ve projedeki görevleri teslim edilen MS Teams klasörü içerisindeki proje_takimi.txt dosyasında detaylıca belirtilmiştir.
