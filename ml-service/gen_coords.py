
import airportsdata

# Cargar base de datos
try:
    airports = airportsdata.load('IATA')
except Exception as e:
    print(f"Error loading airportsdata: {e}")
    exit(1)

# Listas extraidas de AirlineConfig.java
delta = ["ABQ","AGS","ALB","ANC","ATL","ATW","AUS","AVL","BDL","BGR","BHM","BIL","BIS","BNA","BOI","BOS","BTR","BTV","BUF","BUR","BWI","BZN","CAE","CHA","CHO","CHS","CID","CLE","CLT","CMH","COS","CVG","DAB","DAL","DAY","DCA","DEN","DFW","DLH","DSM","DTW","ECP","EGE","ELP","EWR","EYW","FAI","FAR","FAT","FAY","FCA","FLL","FSD","GEG","GNV","GPT","GRB","GRR","GSO","GSP","HDN","HNL","HOU","HPN","HRL","HSV","IAD","IAH","ICT","IDA","ILM","IND","JAC","JAN","JAX","JFK","JNU","KOA","LAS","LAX","LEX","LGA","LGB","LIH","LIT","MCI","MCO","MDT","MDW","MEM","MIA","MKE","MLB","MOB","MSN","MSO","MSP","MSY","MTJ","MYR","OAK","OGG","OKC","OMA","ONT","ORD","ORF","PBI","PDX","PHL","PHX","PIT","PNS","PSC","PSP","PVD","PWM","RAP","RDU","RIC","RNO","ROA","ROC","RSW","SAN","SAT","SAV","SBA","SBN","SDF","SEA","SFO","SGF","SHV","SJC","SJU","SLC","SMF","SNA","SRQ","STL","STT","STX","SYR","TLH","TPA","TRI","TUL","TUS","TVC","TYS","VPS","XNA"]
southwest = ["ABQ","ALB","AMA","ATL","AUS","BDL","BHM","BNA","BOI","BOS","BUF","BUR","BWI","BZN","CHS","CLE","CLT","CMH","COS","CRP","CVG","DAL","DCA","DEN","DSM","DTW","ECP","ELP","EUG","FAT","FLL","GEG","GRR","GSP","HDN","HNL","HOU","HRL","IAD","ICT","IND","ISP","ITO","JAN","JAX","KOA","LAS","LAX","LBB","LGA","LGB","LIH","LIT","MAF","MCI","MCO","MDW","MEM","MHT","MIA","MKE","MSP","MSY","MTJ","MYR","OAK","OGG","OKC","OMA","ONT","ORD","ORF","PBI","PDX","PHL","PHX","PIT","PNS","PSP","PVD","PWM","RDU","RIC","RNO","ROC","RSW","SAN","SAT","SAV","SBA","SDF","SEA","SFO","SJC","SJU","SLC","SMF","SNA","SRQ","STL","TPA","TUL","TUS","VPS"]

# Unir y ordenar
all_codes = sorted(list(set(delta + southwest)))

print("// Generado automaticamente desde python airportsdata")
for code in all_codes:
    if code in airports:
        data = airports[code]
        lat = data['lat']
        lon = data['lon']
        name = data['name'].replace('"', '') # Simple quote removal
        print(f'        AIRPORT_COORDINATES.put("{code}", new AirportCoordinates({lat}, {lon}, "{name}"));')
    else:
        print(f'        // Warning: {code} not found in airportsdata')
