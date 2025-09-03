package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        try {
            String filePath = args[0];
            List<Ticket> filteredTickets = loadAndFilterTickets(filePath);

            //Функция для просмотра билет и проверки фильтра (по задания не нужна, сделал для своего удобства)
            //printTickets(filteredTickets);

            //Здесь я считаю время полета для каждого билета
            calculateFlightTimes(filteredTickets);

            //Тут у меня Map, где ключ это авиаперевозчик, а значение это время
            Map<String, Long> minFlightTimeByCarrier = findMinFlightTimeByCarrier(filteredTickets);

            //Разница между средней ценой и медианной
            calculateAndPrintPriceDifference(filteredTickets);

            //Минимальное время полета
            printMinFlightTimes(minFlightTimeByCarrier);

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

    private static List<Ticket> loadAndFilterTickets(String filePath) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        File file = new File(filePath);
        TicketWrapper ticketWrapper = mapper.readValue(file, TicketWrapper.class);

        return ticketWrapper.getTicketList().stream()
                .filter(ticket -> "VVO".equals(ticket.getOrigin()) && "TLV".equals(ticket.getDestination()))
                .collect(Collectors.toList());
    }

    private static void printTickets(List<Ticket> tickets) {
        for (Ticket ticket : tickets) {
            System.out.println(ticket);
        }
    }

    private static void calculateFlightTimes(List<Ticket> tickets) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy HH:mm");

        for (Ticket ticket : tickets) {
            String departureDateTime = ticket.getDepartureDate() + " " + ticket.getDepartureTime();
            Date departure = dateFormat.parse(departureDateTime);

            String arrivalDateTime = ticket.getArrivalDate() + " " + ticket.getArrivalTime();
            Date arrival = dateFormat.parse(arrivalDateTime);

            long ms = arrival.getTime() - departure.getTime();
            long minutes = ms / (60 * 1000);

            ticket.setFlightTime(minutes);
        }
    }

    private static Map<String, Long> findMinFlightTimeByCarrier(List<Ticket> tickets) {
        Map<String, Long> minFlightTimeByCarrier = new HashMap<>();

        for (Ticket ticket : tickets) {
            String carrier = ticket.getCarrier();
            Long flightTime = ticket.getFlightTime();

            if (flightTime != null) {
                if (!minFlightTimeByCarrier.containsKey(carrier) ||
                        flightTime < minFlightTimeByCarrier.get(carrier)) {
                    minFlightTimeByCarrier.put(carrier, flightTime);
                }
            }
        }

        return minFlightTimeByCarrier;
    }

    private static void calculateAndPrintPriceDifference(List<Ticket> tickets) {
        List<Integer> prices = new ArrayList<>();
        for (Ticket ticket : tickets) {
            prices.add(ticket.getPrice());
        }

        double averagePrice = calculateAverage(prices);
        double medianPrice = calculateMedian(prices);
        double difference = averagePrice - medianPrice;

        System.out.printf("Средняя цена: %.2f рублей\n", averagePrice);
        System.out.printf("Медианная цена: %.2f рублей\n", medianPrice);
        System.out.printf("Разница между средней и медианной ценой: %.2f рублей\n", difference);
    }

    private static void printMinFlightTimes(Map<String, Long> minFlightTimeByCarrier) {
        for (Map.Entry<String, Long> entry : minFlightTimeByCarrier.entrySet()) {
            long hours = entry.getValue() / 60;
            long minutes = entry.getValue() % 60;
            System.out.printf("%s: %d часов %d минут \n", entry.getKey(), hours, minutes);
        }
    }
    private static double calculateAverage(List<Integer> prices) {
        int sum = 0;
        for (int price : prices) {
            sum += price;
        }
        return (double) sum / prices.size();
    }

    private static double calculateMedian(List<Integer> prices) {
        List<Integer> sortedPrices = new ArrayList<>(prices);
        Collections.sort(sortedPrices);

        int size = sortedPrices.size();
        if (size % 2 == 1) {
            return sortedPrices.get(size / 2);
        } else {
            return (sortedPrices.get(size / 2 - 1) + sortedPrices.get(size / 2)) / 2.0;
        }
    }
}