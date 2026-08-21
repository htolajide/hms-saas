import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import { Activity, Plus, ArrowLeft, Loader2, FlaskConical } from 'lucide-react';
import { getHospitalId } from '../utils/auth';

export default function LaboratoryManagement() {
    const navigate = useNavigate();
    const hospitalId = getHospitalId();
    
    const [tests, setTests] = useState([]);
    const [pendingOrders, setPendingOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [showForm, setShowForm] = useState(false);
    const [resultText, setResultText] = useState({});
    
    // Dynamic lab test categories from Settings
    const [labCategories, setLabCategories] = useState([]);
    
    // Form state - NO hardcoded hospitalId
    const [formData, setFormData] = useState({ name: '', categoryKey: '', price: 0 });

    const fetchData = async () => {
        try {
            const [testRes, orderRes] = await Promise.all([
                api.get(`/laboratory/tests?hospitalId=${hospitalId}`),
                api.get(`/laboratory/orders/pending?hospitalId=${hospitalId}`)
            ]);
            setTests(testRes.data);
            setPendingOrders(orderRes.data);
        } catch (err) { console.error(err); } finally { setLoading(false); }
    };

    // Fetch dynamic categories when form opens
    useEffect(() => {
        if (showForm) {
            api.get(`/settings?hospitalId=${hospitalId}&category=LAB_TEST_CATEGORY`)
                .then(res => setLabCategories(res.data))
                .catch(err => console.error("Failed to load lab categories", err));
        }
    }, [showForm]);

    useEffect(() => { fetchData(); }, []);

    const handleSubmit = async (e) => {
        e.preventDefault();
        await api.post('/laboratory/tests', { ...formData, hospitalId });
        setShowForm(false);
        setFormData({ name: '', categoryKey: '', price: 0 });
        fetchData();
    };

    const submitResult = async (orderId) => {
        if (!resultText[orderId]?.trim()) return alert("Enter a result first");
        await api.post(`/laboratory/orders/${orderId}/result`, { result: resultText[orderId] });
        setResultText(prev => ({...prev, [orderId]: ''}));
        fetchData();
    };

    return (
        <div className="space-y-6">
            <button onClick={() => navigate('/dashboard')} className="flex items-center gap-2 text-gray-500 hover:text-primary transition text-sm">
                <ArrowLeft className="h-4 w-4" /> Back to Dashboard
            </button>
            
            <div className="flex justify-between items-center">
                <div>
                    <h2 className="text-3xl font-bold text-gray-800 flex items-center gap-3">
                        <div className="bg-orange-100 p-2 rounded-lg"><Activity className="h-7 w-7 text-orange-600" /></div>
                        Laboratory Management
                    </h2>
                    <p className="text-gray-500 mt-2 ml-12">Manage lab tests, requests, and results.</p>
                </div>
                <button onClick={() => setShowForm(!showForm)} className="flex items-center gap-2 bg-orange-600 hover:bg-orange-700 text-white px-6 py-3 rounded-lg font-medium">
                    <Plus className="h-5 w-5" /> Add Lab Test
                </button>
            </div>

            {showForm && (
                <form onSubmit={handleSubmit} className="bg-white p-6 rounded-2xl shadow-sm grid grid-cols-1 md:grid-cols-4 gap-4">
                    <div className="md:col-span-2">
                        <label className="block text-sm font-medium text-gray-700 mb-1">Test Name</label>
                        <input placeholder="e.g., Malaria Parasite" value={formData.name} 
                            onChange={e => setFormData({...formData, name: e.target.value})} 
                            required className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" />
                    </div>
                    
                    {/* ✅ DYNAMIC CATEGORY SELECT */}
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Test Category</label>
                        <select value={formData.categoryKey} 
                            onChange={e => setFormData({...formData, categoryKey: e.target.value})}
                            required
                            className="w-full px-3 py-2 border bg-white rounded-lg focus:ring-2 focus:ring-orange-500 outline-none"
                        >
                            <option value="">Select category...</option>
                            {labCategories.map(cat => (
                                <option key={cat.id} value={cat.key}>{cat.label}</option>
                            ))}
                        </select>
                    </div>
                    
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Price (₦)</label>
                        <input type="number" min="0" step="0.01" placeholder="0.00" value={formData.price} 
                            onChange={e => setFormData({...formData, price: Number(e.target.value)})} 
                            required className="w-full px-3 py-2 border rounded-lg focus:ring-2 focus:ring-orange-500 outline-none" />
                    </div>
                    
                    <div className="md:col-span-4 flex justify-end">
                        <button type="submit" className="bg-orange-600 text-white px-6 py-2 rounded-lg hover:bg-orange-700 transition font-medium">
                            Save Lab Test
                        </button>
                    </div>
                </form>
            )}

            {loading ? <Loader2 className="animate-spin mx-auto" /> : (
                <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                    {/* Test Catalog */}
                    <div className="bg-white rounded-2xl p-6">
                        <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                            <FlaskConical className="h-5 w-5" /> Test Catalog
                        </h3>
                        <div className="space-y-2">
                            {tests.length === 0 ? <p className="text-gray-500 text-sm">No tests configured yet.</p> :
                            tests.map(t => (
                                <div key={t.id} className="flex justify-between items-center p-3 bg-gray-50 rounded-lg">
                                    <div>
                                        <p className="font-medium text-gray-900">{t.name}</p>
                                        {/* Display category LABEL from settings, not raw key */}
                                        <p className="text-xs text-gray-500">{t.categoryLabel}</p>
                                    </div>
                                    <span className="text-sm font-bold text-gray-700">₦{t.price?.toLocaleString()}</span>
                                </div>
                            ))}
                        </div>
                    </div>

                    {/* Pending Requests */}
                    <div className="bg-white rounded-2xl p-6">
                        <h3 className="text-lg font-bold text-gray-800 mb-4 flex items-center gap-2">
                            <Activity className="h-5 w-5" /> Pending Requests
                        </h3>
                        <div className="space-y-4">
                            {pendingOrders.length === 0 ? <p className="text-gray-500 text-sm">No pending requests.</p> : 
                            pendingOrders.map(order => (
                                <div key={order.id} className="border border-gray-200 rounded-lg p-4">
                                    <div className="flex justify-between mb-2">
                                        <span className="font-bold text-gray-900">{order.testName}</span>
                                        <span className="text-xs text-gray-500">{order.patientName}</span>
                                    </div>
                                    <div className="flex gap-2">
                                        <input placeholder="Enter Result..." 
                                            value={resultText[order.id] || ''} 
                                            onChange={e => setResultText({...resultText, [order.id]: e.target.value})} 
                                            className="flex-1 px-3 py-1 border rounded text-sm focus:ring-2 focus:ring-orange-500 outline-none" />
                                        <button onClick={() => submitResult(order.id)} 
                                            className="bg-green-600 text-white px-3 py-1 rounded text-sm hover:bg-green-700 whitespace-nowrap">
                                            Post Result
                                        </button>
                                    </div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}