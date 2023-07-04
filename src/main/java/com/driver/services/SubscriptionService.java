package com.driver.services;


import com.driver.EntryDto.SubscriptionEntryDto;
import com.driver.model.Subscription;
import com.driver.model.SubscriptionType;
import com.driver.model.User;
import com.driver.repository.SubscriptionRepository;
import com.driver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SubscriptionService {

    @Autowired
    SubscriptionRepository subscriptionRepository;

    @Autowired
    UserRepository userRepository;

    public Integer buySubscription(SubscriptionEntryDto subscriptionEntryDto){

        //Save The subscription Object into the Db and return the total Amount that user has to pay
        Subscription subscription=new Subscription();
        subscription.setSubscriptionType(subscriptionEntryDto.getSubscriptionType());
        subscription.setNoOfScreensSubscribed(subscriptionEntryDto.getNoOfScreensRequired());
        subscription.setStartSubscriptionDate(new Date());

        int total=0;
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            total = 500 + 200*(subscriptionEntryDto.getNoOfScreensRequired());
        }
        if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            total = 800 + 250*(subscriptionEntryDto.getNoOfScreensRequired());
        }
        if(subscription.getSubscriptionType().equals(SubscriptionType.ELITE)){
            total = 1000 + 300*(subscriptionEntryDto.getNoOfScreensRequired());
        }
        subscription.setTotalAmountPaid(total);

        User user=userRepository.findById(subscriptionEntryDto.getUserId()).get();
        user.setSubscription(subscription);
        subscription.setUser(user);
        subscriptionRepository.save(subscription);
        userRepository.save(user);
        return total;
    }

    public Integer upgradeSubscription(Integer userId)throws Exception{

        //If you are already at an ElITE subscription : then throw Exception ("Already the best Subscription")
        //In all other cases just try to upgrade the subscription and tell the difference of price that user has to pay
        //update the subscription in the repository
        User user=userRepository.findById(userId).get();
        if(user.getSubscription().getSubscriptionType().equals(SubscriptionType.ELITE)){
            throw new Exception("Already the best Subscription");
        }
        int diff=0;
        Subscription subscription=user.getSubscription();
        if(subscription.getSubscriptionType().equals(SubscriptionType.BASIC)){
            subscription.setSubscriptionType(SubscriptionType.PRO);
            int total=800 + 250*(subscription.getNoOfScreensSubscribed());
            diff=total-subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(total);
        }
        if(subscription.getSubscriptionType().equals(SubscriptionType.PRO)){
            subscription.setSubscriptionType(SubscriptionType.ELITE);
            int total=1000 + 300*(subscription.getNoOfScreensSubscribed());
            diff=total-subscription.getTotalAmountPaid();
            subscription.setTotalAmountPaid(total);
        }
        user.setSubscription(subscription);
        subscriptionRepository.save(subscription);
        userRepository.save(user);

        return diff;
    }

    public Integer calculateTotalRevenueOfHotstar(){

        //We need to find out total Revenue of hotstar : from all the subscriptions combined
        //Hint is to use findAll function from the SubscriptionDb
        List<Subscription> subscriptionList=subscriptionRepository.findAll();
        int total=0;
        for(Subscription subscription:subscriptionList){
            total+=subscription.getTotalAmountPaid();
        }

        return total;
    }

}
