import { NavigationContainer } from '@react-navigation/native';
import { createNativeStackNavigator } from '@react-navigation/native-stack';

import Home from './component/Home';
import SignInNew from './component/SignInNew';
import SignUpNew from './component/SignUpNew';
import ForgetPassword from './component/ForgetPassword';
import ConfirmPassword from './component/ConfirmPassword';
import Order from './component/Order';
import Checkout from './component/Checkout';
import Payment from './component/Payment';
import Utility from './component/Utility';

const Stack = createNativeStackNavigator();

export default function App() {
  return (
    
    <NavigationContainer>
      <Stack.Navigator screenOptions={{headerShown : false}}>  
          
      <Stack.Screen name="Home"      component={Home}      />
      <Stack.Screen name="Checkout"  component={Checkout}     />

      
      <Stack.Screen name="Order"     component={Order}     />
      
      <Stack.Screen name="Payment"   component={Payment}     />    
      <Stack.Screen name="Utility"   component={Utility}     /> 
      
          
               
          
          <Stack.Screen name="SignInNew" component={SignInNew} />
          
          
          
          
          
          <Stack.Screen name="SignUpNew" component={SignUpNew} />
          <Stack.Screen name="ForgetPassword"  component={ForgetPassword}  />
          <Stack.Screen name="ConfirmPassword" component={ConfirmPassword} />
          
      </Stack.Navigator>
    </NavigationContainer>

  );
}