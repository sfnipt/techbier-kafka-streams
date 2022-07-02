package ch.ipt.kafka.exercise8.joinaggregate.solution;

import ch.ipt.kafka.techbier.Account;
import ch.ipt.kafka.techbier.AccountBalance;
import ch.ipt.kafka.techbier.AccountPayment;
import ch.ipt.kafka.techbier.Payment;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.KTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;


//@Component
public class KafkaStreamsJoinAggregateSolution {

    @Value("${source-topic-transactions}")
    private String sourceTransactions;
    @Value("${source-topic-accounts}")
    private String sourceAccounts;

    @Value("${INITIALS}")
    private String initial;

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStreamsJoinAggregateSolution.class);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        String sinkTopic = "sum-transactions-per-account-" + initial;

        //Compute the total of all payments for every single customer and create a new schema containing the account information plus the sum of the transactions
        KStream<String, Payment> transactionStream = streamsBuilder.stream(sourceTransactions);
        KStream<String, Account> accountStream = streamsBuilder.stream(sourceAccounts);

        KTable<String, Account> accountTable = accountStream.toTable();
        transactionStream.toTable()
                .join(accountTable, t -> t.getAccountId().toString(), this::joinAccountTransaction)
                .toStream()
                .groupBy((k, v) -> v.getAccountId().toString())
                .aggregate(
                        AccountBalance::new, // Initial Value
                        (key, payment, current) -> updateBalance(payment, current)
                )
                .toStream()
                .peek((key, value) -> LOGGER.info("Message with sum per account: key={}, value={}", key, value))
                .to(sinkTopic);

        LOGGER.info(String.valueOf(streamsBuilder.build().describe()));
    }

    private AccountBalance updateBalance(AccountPayment payment, AccountBalance current) {
        current.setAccountId(payment.getAccountId());
        current.setSurname(payment.getSurname());
        current.setLastname(payment.getLastname());
        current.setStreet(payment.getStreet());
        current.setCity(payment.getCity());
        current.setBalance(current.getBalance() + payment.getAmount());

        return current;
    }

    private AccountPayment joinAccountTransaction(Payment payment, Account account) {
        return new AccountPayment(account.getAccountId(),
                account.getSurname(),
                account.getLastname(),
                account.getStreet(),
                account.getCity(),
                payment.getId(),
                payment.getCardNumber(),
                payment.getCardType(),
                payment.getAmount());
    }


}