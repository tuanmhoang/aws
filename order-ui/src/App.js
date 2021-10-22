import { useState } from 'react'
import './App.css';
import dummyData from './data/data.json';


function App() {
  const items = dummyData.items; 
  //console.log(JSON.stringify(items));

  const [selectedItem, setSelectedItem] = useState(items[0])
  const [cart, setCart] = useState([])
  const [quantity, setQuantity] = useState(0)
  const handleItemChange = ((e) => {
    setSelectedItem(items.find(i => i.id === parseInt(e.target.value)))
    setQuantity(0)
  })

  const handleChangeQuantity = (e) => {
    setQuantity(e.target.value)
  }

  const handleOrderNow = () => {
    console.log('-----------------------------------------')
    console.log('Data to send: ', JSON.stringify(cart))
    fetch('http://localhost:9090/v1/order', {
      method: 'POST',
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(cart)
    })
      .then(res => console.log(res))
      .catch(error => console.log(error))
  }

  const handleAddToCart = () => {
    console.log('-----------------------------------------')
    console.log('Order: ' + selectedItem.name + ' - quantity: ' + quantity)
    let newCart = [...cart]
    const selectedId = selectedItem.id
    // check if exist
    if (cart.some(i => i.id === selectedId)) {
      // if yes, sum
      console.log('exist')
      for(let i = 0; i < newCart.length; i++){
        if (newCart[i].id === selectedId) {
          newCart[i].quantity = parseInt(newCart[i].quantity) + parseInt(quantity);          
        }
      }
     
    } else { // if no, add to cart
      console.log('not exist')      
      newCart.push({
        id: selectedItem.id,        
        quantity: parseInt(quantity)
      });     
    }
    setCart(newCart);
  }

  return (
    <div className="App">
      <header className="App-header">
        <h2>Order your items</h2>
      </header>

      < select
        onChange={handleItemChange}>
        {
          items.map(item =>
            <option
              key={item.id}
              value={item.id}>
              {item.name}
            </option>
          )
        }
      </select >
      <br />
      <div>
        <h2>{selectedItem.name}</h2>
        <img
          className="photo"
          src={selectedItem.imgUrl}
          alt={selectedItem.name}
        />
        <br />
        <br />
        {selectedItem.description}
        <br />
        <br />
        <label >
          Quantity
          <input
            type="number"
            value={quantity}
            onChange={handleChangeQuantity}
          />
          {selectedItem.type === 'LIQUID' ? ' ml' : ' item(s)'}
        </label>
        <br />
        <br />
        <button onClick={handleAddToCart}>Add to cart</button>

      </div>

      <h2>Your cart</h2>
      {
        cart.map(i =>
          <p key={i.id} >ID: {i.id} - Quantity: {i.quantity}</p>
        )
      }
      <br />
      <button onClick={handleOrderNow}>Order now</button>
    </div>
  );
}

export default App;
