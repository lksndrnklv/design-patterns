package prototype;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {

        ServicePackage sp1 = new ServicePackage("post-paid");
        sp1.addService(new VoiceService(500.0, 500.0, 30, 3.9, 0.0, 0));
        sp1.addService(new SmsService(0.0, 50, 4.9));
        sp1.addService(new DataService(300.0, 5, 100 * 1024, 4.0));
        sp1.addService(new RoamService());

        ServicePackage sp2 = new ServicePackage("pre-paid");
        sp2.addService(new VoiceService(0.0, 0.0, 60, 6.9, 6.9, 60));
        sp2.addService(new SmsService(0.0, 0, 5.0));

        ServicePackage sp3 = new ServicePackage("pre-paid-data+");

        PackageFactory.addPrototype(sp1);
        PackageFactory.addPrototype(sp2);
        PackageFactory.addPrototype(sp3);

        ServicePackage psp1 = PackageFactory.findAndClone("post-paid");
        ServicePackage psp2 = PackageFactory.findAndClone("pre-paid");

        printServicePackageContent(psp1);
        printServicePackageContent(psp2);

        //Dokolku sakame za nekoj paket da izmenime cena na nekoj servis vo ramki na toj paket toa mozhe da se napravi na sledniot nachin:
        ServicePackage psp3 = PackageFactory.findAndClone("post-paid");
        psp3.updateBaseServiceSubscriptionPriceForServiceWithName(100.0, "Voice");

        printServicePackageContent(psp3);

    }

    private static void printServicePackageContent(ServicePackage psp1) {
        System.out.printf("List of services & subscription prices in package: %s%n", psp1.getName());
        psp1.listService();
        System.out.printf("Total: %.0f%n%n", psp1.packagePrice());
    }
}

abstract class BasicService {
    protected double baseServiceSubscriptionPrice;

    protected BasicService(double baseServiceSubscriptionPrice) {
        this.baseServiceSubscriptionPrice = baseServiceSubscriptionPrice;
    }

    public abstract BasicService copy();
    public abstract String getName();

    public double getSubscriptionPrice() {
        return baseServiceSubscriptionPrice;
    }

    public void setNewBaseServiceSubscriptionPrice(double price) {
        this.baseServiceSubscriptionPrice = price;
    }
}

class VoiceService extends BasicService {
    private double freeMinutes;
    private int unit;
    private double pricePerMin;
    private double pricePerCall;
    private int freeSecondsInCall;

    public VoiceService(double servicePrice,
                        double freeMinutes,
                        int unit,
                        double pricePerMin,
                        double pricePerCall,
                        int freeSecondsInCall) {
        super(servicePrice);
        this.freeMinutes = freeMinutes;
        this.unit = unit;
        this.pricePerMin = pricePerMin;
        this.pricePerCall = pricePerCall;
        this.freeSecondsInCall = freeSecondsInCall;
    }

    @Override
    public VoiceService copy() {
        return new VoiceService(this.baseServiceSubscriptionPrice,
                this.freeMinutes,
                this.unit,
                this.pricePerMin,
                this.pricePerCall,
                this.freeSecondsInCall);
    }

    @Override
    public String getName() {
        return "Voice";
    }
}

class SmsService extends BasicService {
    private int freeMessages;
    private double pricePerMessage;

    public SmsService(double servicePrice,
                      int freeMessages,
                      double pricePerMessage) {
        super(servicePrice);
        this.freeMessages = freeMessages;
        this.pricePerMessage = pricePerMessage;
    }

    @Override
    public SmsService copy() {
        return new SmsService(this.baseServiceSubscriptionPrice,
                this.freeMessages,
                this.pricePerMessage);
    }

    @Override
    public String getName() {
        return "SMS";
    }
}

class DataService extends BasicService {
    int freeDataTransferSize;
    int unit;
    double pricePerUnit;
    int maxDownloadSpeed;
    int maxUploadSpeed;

    public DataService(double servicePrice, int freeDataTransferSize, int unit, double pricePerUnit) {
        super(servicePrice);
        this.freeDataTransferSize = freeDataTransferSize;
        this.unit = unit;
        this.pricePerUnit = pricePerUnit;
        this.maxDownloadSpeed = Integer.MAX_VALUE;
        this.maxUploadSpeed = Integer.MAX_VALUE;
    }

    public DataService(double servicePrice,
                       int freeDataTransferSize,
                       int unit,
                       double pricePerUnit,
                       int maxDownloadSpeed,
                       int maxUploadSpeed) {
        super(servicePrice);
        this.freeDataTransferSize = freeDataTransferSize;
        this.unit = unit;
        this.pricePerUnit = pricePerUnit;
        this.maxDownloadSpeed = maxDownloadSpeed;
        this.maxUploadSpeed = maxUploadSpeed;
    }

    @Override
    public DataService copy() {
        return new DataService(this.baseServiceSubscriptionPrice,
                this.freeDataTransferSize,
                this.unit,
                this.pricePerUnit,
                this.maxDownloadSpeed,
                this.maxUploadSpeed);
    }

    @Override
    public String getName() {
        return "Data";
    }
}

class FaxService extends BasicService {

    public FaxService(double servicePrice) {
        super(servicePrice);
    }

    @Override
    public FaxService copy() {
        return new FaxService(this.baseServiceSubscriptionPrice);
    }

    @Override
    public String getName() {
        return "Fax";
    }
}

class RoamService extends BasicService {

    public RoamService(){
        super(0.0);
    }

    public RoamService(double servicePrice) {
        super(servicePrice);
    }

    @Override
    public RoamService copy() {
        return new RoamService(this.baseServiceSubscriptionPrice);
    }

    @Override
    public String getName() {
        return "Roam";
    }
}

class ServicePackage {
    private final String name;
    private final List<BasicService> services = new ArrayList<>();

    public ServicePackage(String name) {
        this.name = name;
    }

    public void addService(BasicService service) {
        this.services.add(service);
    }

    public String getName() {
        return name;
    }

    public ServicePackage copy() {
        ServicePackage servicePackage = new ServicePackage(this.name);
        for (BasicService basicService : services) {
            servicePackage.addService(basicService.copy());
        }
        return servicePackage;
    }

    public void listService() {
        for (int i = 0; i < services.size(); i++) {
            System.out.printf("%d. %s %.0f%n", i + 1, services.get(i).getName(), services.get(i).getSubscriptionPrice());
        }
    }

    public double packagePrice() {
        return services.stream().map(BasicService::getSubscriptionPrice).reduce(Double::sum).get();
    }

    public void updateBaseServiceSubscriptionPriceForServiceWithName(double newPrice, String serviceName) {
        services.stream().filter(x -> x.getName().equals(serviceName)).findFirst().get().setNewBaseServiceSubscriptionPrice(newPrice);
    }
}

class PackageFactory {
    private static final List<ServicePackage> packages = new ArrayList<>();


    public PackageFactory() {
    }

    public static void addPrototype(ServicePackage servicePackage) {
        packages.add(servicePackage);
    }

    public static ServicePackage findAndClone(String name) {
        ServicePackage servicePackage = packages.stream()
                .filter(sp -> sp.getName().equals(name))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Could not find package with name: " + name));
        return servicePackage.copy();
    }
}
